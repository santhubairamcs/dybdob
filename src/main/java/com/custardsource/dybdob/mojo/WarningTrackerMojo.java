package com.custardsource.dybdob.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Goal which fails the build if the warning count has increased since last successful execution
 *
 * @goal trackwarnings
 * @phase compile
 */
public class WarningTrackerMojo extends AbstractMojo {
    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}/javac.out"
     * @required
     */
    private File warningLog;


    public void execute() throws MojoExecutionException {
        int oldCount = oldWarningCount();
        int warningCount = new WarningCounter(warningLog).warningCount();
        if (warningCount < oldCount) {
            getLog().info(String.format("Well done! Warning count decreased from %s to %s", oldCount, warningCount));
            lowerWarningCount();
        } else if (oldCount == warningCount) {
            getLog().info(String.format("Warning count remains steady at %s", warningCount));
        } else {
            throw new MojoExecutionException(String.format("Failing build with warning count %s higher than previous mark of %s; see %s for warning details", warningCount, oldCount, warningLog));
        }
    }

    private void lowerWarningCount() {
    }

    private int oldWarningCount() {
        return 2;
    }
}
