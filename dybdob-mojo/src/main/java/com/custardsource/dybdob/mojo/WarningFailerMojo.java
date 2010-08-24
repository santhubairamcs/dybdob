package com.custardsource.dybdob.mojo;

import java.io.File;

import com.custardsource.dybdob.WarningRecord;
import com.custardsource.dybdob.detectors.WarningDetector;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which fails the build if a compiler log has indicated any warnings
 *
 * @goal failonwarnings
 * @phase verify
 */
public class WarningFailerMojo extends DybdobMojo {
    @Override
    protected void checkSingleRecord(WarningRecord record, File logFile, WarningDetector warningDetector) throws MojoExecutionException {
        int warningCount = record.warningCount();
        if (warningCount > 0) {
            addFailure(String.format("Failing build with warning count %s for metric %s, no warnings permitted; see %s for warning details", warningCount, record.source(), logFile));
        }
    }

    @Override
    protected void initialize() throws MojoExecutionException {
    }

    @Override
    protected void tearDown() {
    }

}
