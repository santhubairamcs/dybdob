package com.custardsource.dybdob.mojo;

import java.io.File;
import java.util.List;

import com.custardsource.dybdob.WarningRecord;
import com.custardsource.dybdob.WarningRecordRepository;
import com.custardsource.dybdob.detectors.WarningDetector;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which fails the build if the warning count has increased since last successful execution
 *
 * @goal trackwarnings
 * @phase verify
 */
public class WarningTrackerMojo extends DybdobMojo {
    private static final int DIFF_CONTEXT_LINES = 5;
    private static final String DIFF_NEW_FILE_NAME = "new";
    private static final String DIFF_OLD_FILE_NAME = "old";

    private static enum OperationMode {
        CHECK,
        TRACK,
        FORCE
    }

    /**
     * DB driver to use when logging warnings
     *
     * 
     * @parameter expression="${dybdob.db.driver}"
     * @required
     */
    private String jdbcDriver;

    /**
     * DB connection string to use when logging warnings
     *
     * @parameter expression="${dybdob.db.connection}"
     * @required
     */
    private String jdbcConnection;

    /**
     * DB username to use when logging warnings
     *
     * @parameter expression="${dybdob.db.user}"
     * @required
     */
    private String jdbcUser;

    /**
     * DB password to use when logging warnings
     *
     * @parameter expression="${dybdob.db.password}"
     * @required
     */
    private String jdbcPassword;

    /**
     * Mode of operation. Should be one of "count" (record count only, for later use), "check" (check
     * the count, fail build if increased, but don't write updates to DB), "track" (check the count,
     * fail if increased, write decreases back to the database), or "force" (check the count, never
     * fail the build, always write back to the database — used as a one-off process to allow warning
     * count to increase)
     *
     * @parameter expression="${dybdob.mode}"
     */
    private String mode = "";

    /**
     * Hibernate dialect to use for writing changes
     *
     * @parameter expression="${dybdob.db.dialect}"
     * @required
     */
    private String hibernateDialect;


    private OperationMode operationMode = OperationMode.CHECK;

    private WarningRecordRepository repository;

    protected void initialize() throws MojoExecutionException {
        setupRepository();
        if (!Strings.isNullOrEmpty(mode)) {
            operationMode = OperationMode.valueOf(mode.toUpperCase());
        }
        getLog().info("Running in mode " + operationMode);
    }

    private void setupRepository() throws MojoExecutionException {
        repository = new WarningRecordRepository(jdbcDriver, jdbcConnection, jdbcUser, jdbcPassword, hibernateDialect) ;
    }


    @Override
    protected void checkSingleRecord(WarningRecord record, File logFile, WarningDetector warningDetector) throws MojoExecutionException {
        WarningRecord oldCount = oldWarningCountFor(record);

        boolean readOnly = (operationMode == OperationMode.CHECK);

        if (oldCount == null) {
            if (readOnly) {
                getLog().warn(String.format("Unable to obtain old warning count for %s; may be first run of this artifact version. New count would be %s", record.source(), record.warningCount()));
            } else {
                getLog().info(String.format("Unable to obtain old warning count for %s; may be first run of this artifact version. New count is %s", record.source(), record.warningCount()));
                recordWarningCountInDatabase(record);
            }
        }
        else if (record.warningCount() < oldCount.warningCount()) {
            getLog().info(String.format("Well done! Warning count for %s decreased from %s to %s", record.source(), oldCount.warningCount(), record.warningCount()));
            if (!readOnly) {
                recordWarningCountInDatabase(record);
            }
        } else if (oldCount.warningCount() == record.warningCount()) {
            getLog().info(String.format("Warning count for %s remains steady at %s", record.source(), record.warningCount()));
        } else {
            String diff = generateDiff(oldCount, record);
            if (operationMode == OperationMode.FORCE) {
                getLog().warn(String.format("Against my better judgement, forcing warning count increase for %s from %s to %s; new warnings:\n%s", record.source(), oldCount.warningCount(), record.warningCount(), diff));
                recordWarningCountInDatabase(record);
            } else {
                getLog().error(String.format("Failing build with increate for %s from %s to %s; changed warnings:\n%s", record.source(), oldCount.warningCount(), record.warningCount(), diff));
                throw new MojoExecutionException(String.format("Failing build with %s warning count %s higher than previous mark of %s; see %s for warning details", record.source(), record.warningCount(), oldCount.warningCount(), logFile));
            }
        }
    }

    private String generateDiff(WarningRecord oldCount, WarningRecord record) {
        if (oldCount == null || oldCount.toolOutput() == null) {
            return record.toolOutput();
        }
        Splitter splitter = Splitter.on(CharMatcher.anyOf("\r\n"));

        List<String> oldLines = Lists.newArrayList(splitter.split(oldCount.toolOutput()));
        List<String> newLines = Lists.newArrayList(splitter.split(record.toolOutput()));
        Patch diffs = DiffUtils.diff(oldLines, newLines);
        return Joiner.on("\n").join(DiffUtils.generateUnifiedDiff(DIFF_OLD_FILE_NAME, DIFF_NEW_FILE_NAME, oldLines,
                diffs, DIFF_CONTEXT_LINES));
    }

    private void recordWarningCountInDatabase(WarningRecord record) {
        repository.recordWarningCount(record);

    }

    private WarningRecord oldWarningCountFor(WarningRecord record) {
        return repository.lastWarningRecordFor(record);
    }

    @Override
    protected void tearDown() {
    }
}
