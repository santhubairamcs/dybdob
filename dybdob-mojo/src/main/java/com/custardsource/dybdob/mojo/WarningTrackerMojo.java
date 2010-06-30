package com.custardsource.dybdob.mojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.custardsource.dybdob.ProjectVersion;
import com.custardsource.dybdob.WarningCounter;
import com.custardsource.dybdob.WarningRecordRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

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
     * @parameter expression="${projectWrapper.build.directory}/javac.out"
     * @required
     */
    private File warningLog;

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
     * Should we write our changes back to the database?
     *
     * @parameter expression="${dybdob.readonly}"
     */
    private boolean readOnly = true;

    /**
     * Hibernate dialect to use for writing changes
     *
     * @parameter expression="${dybdob.db.dialect}"
     * @required
     */
    private String hibernateDialect;

    /**
     * @parameter default-value="${project}"
     * */
    private org.apache.maven.project.MavenProject mavenProject;

    private ProjectVersion projectVersion;
    private WarningRecordRepository repository;


    public void execute() throws MojoExecutionException {
        if (!mavenProject.getPackaging().equals("jar")) {
            getLog().info("Skipping warning count for non-jar packaging type " + mavenProject.getPackaging());
            return;
        }
        // mavenProject.getProperties().remove("dybdob.warnings.count");
        projectVersion = new ProjectVersion(mavenProject.getGroupId(), mavenProject.getArtifactId(), mavenProject.getVersion());
        setupRepository();
        checkWarningCounts();
    }

    private void setupRepository() throws MojoExecutionException {
        repository = new WarningRecordRepository(jdbcDriver, jdbcConnection, jdbcUser, jdbcPassword, hibernateDialect) ;
    }

    private void checkWarningCounts() throws MojoExecutionException {
        Integer oldCount = oldWarningCount();
        int warningCount = new WarningCounter(warningLog).warningCount();
        if (oldCount == null) {
            if (readOnly) {
                getLog().warn(String.format("Unable to obtain old warning count; may be first run of this artifact version. New count would be %s", warningCount));
            } else {
                getLog().info(String.format("Unable to obtain old warning count; may be first run of this artifact version. New count is %s", warningCount));
                lowerWarningCount(warningCount);
            }
        }
        else if (warningCount < oldCount) {
            getLog().info(String.format("Well done! Warning count decreased from %s to %s", oldCount, warningCount));
            if (!readOnly) {
                lowerWarningCount(warningCount);
            }
        } else if (oldCount == warningCount) {
            getLog().info(String.format("Warning count remains steady at %s", warningCount));
        } else {
            throw new MojoExecutionException(String.format("Failing build with warning count %s higher than previous mark of %s; see %s for warning details", warningCount, oldCount, warningLog));
        }

        mavenProject.getProperties().setProperty("dybdob.warnings.count", String.valueOf(warningCount));
        File output = new File(mavenProject.getBuild().getDirectory(), "dybdob.warningcount");
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(output, false), "UTF-8");
            out.write(String.valueOf(warningCount));
            out.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not pass on warning count to subsequent plugins", e);
        }
    }

    private void lowerWarningCount(int warningCount) {
        repository.recordWarningCount(projectVersion, warningCount);

    }

    private Integer oldWarningCount() {
        return repository.lastWarningCount(projectVersion);
    }
}
