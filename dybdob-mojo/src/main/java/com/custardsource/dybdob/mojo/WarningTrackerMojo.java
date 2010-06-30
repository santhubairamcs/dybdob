package com.custardsource.dybdob.mojo;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

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

    private HibernateTemplate hibernateTemplate;
    private DriverManagerDataSource dataSource;
    private ProjectVersion projectVersion;

    public void execute() throws MojoExecutionException {
        if (!mavenProject.getPackaging().equals("jar")) {
            getLog().info("Skipping warning count for non-jar packaging type " + mavenProject.getPackaging());
            return;
        }
        projectVersion = new ProjectVersion(mavenProject.getGroupId(), mavenProject.getArtifactId(), mavenProject.getVersion());
        setupDatabaseTemplate();
        checkWarningCounts();
    }

    private void setupDatabaseTemplate() throws MojoExecutionException {
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Cannot load specified JDBC driver: " + jdbcDriver, e);
        }
        dataSource = new DriverManagerDataSource(jdbcConnection, jdbcUser, jdbcPassword);

        AnnotationSessionFactoryBean sessionFactoryBean = new AnnotationSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        Properties config = new Properties();
        config.setProperty("hibernate.dialect", hibernateDialect);
        config.setProperty("hibernate.connection.autocommit", "true");
        config.setProperty("hibernate.hbm2ddl.auto", "update");
        sessionFactoryBean.setHibernateProperties(config);

        sessionFactoryBean.setAnnotatedClasses(new Class<?>[]{WarningRecord.class});
        try {
            sessionFactoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new MojoExecutionException("Could not set up database connection", e);
        }
        hibernateTemplate = new HibernateTemplate((SessionFactory) sessionFactoryBean.getObject());
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
    }

    private void lowerWarningCount(int warningCount) {
        hibernateTemplate.save(WarningRecord.newRecord(projectVersion, warningCount));
    }

    private Integer oldWarningCount() {
        DetachedCriteria c = DetachedCriteria.forClass(WarningRecord.class);
        c.add(Restrictions.eq("groupId", mavenProject.getGroupId()));
        c.add(Restrictions.eq("artifactId", mavenProject.getArtifactId()));
        c.add(Restrictions.eq("version", mavenProject.getVersion()));
        c.addOrder(Order.desc("dateLogged"));
        List<WarningRecord> matches = hibernateTemplate.findByCriteria(c, 0, 1);
        if (matches.isEmpty()) {
            return null;
        }
        return matches.get(0).warningCount();
    }
}
