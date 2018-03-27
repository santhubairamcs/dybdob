package com.custardsource.dybdob.mojo;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.custardsource.dybdob.WarningRecord;
import com.custardsource.dybdob.detectors.CheckstyleDetector;
import com.custardsource.dybdob.detectors.CpdDetector;
import com.custardsource.dybdob.detectors.FindBugsDetector;
import com.custardsource.dybdob.detectors.JavacWarningDetector;
import com.custardsource.dybdob.detectors.PmdDetector;
import com.custardsource.dybdob.detectors.WarningDetector;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

@SuppressWarnings({"JavaDoc"})
public abstract class DybdobMojo extends AbstractMojo {
    private static final List<WarningDetector> KNOWN_DETECTORS = ImmutableList.<WarningDetector>of(
            new JavacWarningDetector(), new CheckstyleDetector(), new CpdDetector(), new PmdDetector(),
            new FindBugsDetector("findbugs"), new FindBugsDetector("findsecbugs"));

    /**
     * @parameter default-value="${project}"
     * @readonly
     * */
    org.apache.maven.project.MavenProject mavenProject;

    /**
     * Skip is enabled
     *
     * @parameter default-value=false
     * @required
     */
    private boolean skip;

    /**
     * Which detectors are enabled
     *
     * @parameter
     * @required
     */
    private List<Detector> detectors;

    private final List<String> failureMessages = Lists.newArrayList();

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        if (!mavenProject.getPackaging().equals("jar")) {
            getLog().info("Skipping warning count for non-jar packaging type " + mavenProject.getPackaging());
            return;
        }

        if (skip) {
            getLog().info("Skipping warning count" + mavenProject.getPackaging());
            return;
        }

        initialize();
        checkWarningCounts();
        tearDown();
    }
    protected abstract void tearDown();

    private void checkWarningCounts() throws MojoExecutionException {
        for (Detector detector : detectors) {
            getLog().debug("Running detector " + detector);
            checkWarningCountForDetector(detector);
        }
        failIfAnyRecordFailures();
    }

    private void checkWarningCountForDetector(Detector detector) throws MojoExecutionException {
        File logFile = detector.logFile();
        WarningDetector warningDetector = getDetectorById(detector.id());

        Collection<WarningRecord> records = warningDetector.getRecords(DybdobMojoUtils.buildProjectVersionFrom(mavenProject), logFile,
                mavenProject.getBasedir());

        for (WarningRecord record : records) {
            if (detector.isCheckEnabled(record.source().getMetric())) {
                checkSingleRecord(record, logFile, warningDetector);
            } else {
                getLog().debug("Skipping check for record " + record);
            }
        }
    }

    protected abstract void checkSingleRecord(WarningRecord record, File logFile, WarningDetector warningDetector) throws MojoExecutionException;

    private WarningDetector getDetectorById(String id) throws MojoExecutionException {
        for (WarningDetector detector : KNOWN_DETECTORS) {
            if (detector.getId().equals(id)) {
                return detector;
            }
        }
        throw new MojoExecutionException("Unknown detector id '" + id + "'; check your configuration");
    }
    
    protected abstract void initialize() throws MojoExecutionException;

    void addFailure(String message) {
        failureMessages.add(message);
    }

    private void failIfAnyRecordFailures() throws MojoExecutionException {
        if (!failureMessages.isEmpty()) {
            throw new MojoExecutionException("Failing with errors: \n" + Joiner.on("\n").join(failureMessages));
        }
    }

}
