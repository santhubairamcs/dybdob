package com.custardsource.dybdob.mojo;

import java.io.File;

import com.custardsource.dybdob.JavacWarningDetector;
import com.custardsource.dybdob.WarningDetector;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which fails the build if a compiler log has indicated any warnings
 *
 * @goal failonwarnings
 * @phase compile
 */
public class WarningFailerMojo extends AbstractMojo {
    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}/javac.out"
     * @required
     */
    private File warningLog;

    /**
     * @parameter default-value="${project}"
     * */
    private org.apache.maven.project.MavenProject mavenProject;


    public void execute() throws MojoExecutionException {
        try {
            int warningCount = new JavacWarningDetector().getRecords(DybdobMojoUtils.buildProjectVersionFrom(mavenProject), warningLog).warningCount();
            if (warningCount > 0) {
                throw new MojoExecutionException(String.format("Failing build with warning count %s, no warnings permitted; see %s for warning details", warningCount, warningLog));
            }
        } catch (WarningDetector.CountException e) {
            throw new MojoExecutionException("Count not count warnings", e);
        }
    }
}
