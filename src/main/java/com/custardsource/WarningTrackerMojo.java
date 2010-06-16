package com.custardsource;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Goal which retrieves a compiler warning count
 *
 * @goal failonwarnings
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
        int warningCount = new WarningCounter(warningLog).warningCount();
        if (warningCount > 0) {
            throw new MojoExecutionException(String.format("Failing build with warning count %s, no warnings permitted; see %s for warning details", warningCount, warningLog));
        }
    }
}