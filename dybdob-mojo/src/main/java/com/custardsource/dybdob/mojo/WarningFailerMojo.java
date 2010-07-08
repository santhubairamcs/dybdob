package com.custardsource.dybdob.mojo;

import java.io.File;

import com.custardsource.dybdob.JavacWarningDetector;
import com.custardsource.dybdob.WarningDetector;
import com.custardsource.dybdob.WarningRecord;
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
        if (!mavenProject.getPackaging().equals("jar")) {
            getLog().info("Skipping warning count for non-jar packaging type " + mavenProject.getPackaging());
            return;
        }

        try {
            for (WarningRecord record : new JavacWarningDetector().getRecords(DybdobMojoUtils.buildProjectVersionFrom(mavenProject), warningLog)) {
                int warningCount = record.warningCount();
                if (warningCount > 0) {
                    throw new MojoExecutionException(String.format("Failing build with warning count %s for metric %s, no warnings permitted; see %s for warning details", warningCount, record.source(), warningLog));
                }
            }
        } catch (WarningDetector.CountException e) {
            throw new MojoExecutionException("Count not count warnings", e);
        }
    }
}
