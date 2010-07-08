package com.custardsource.dybdob;

import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

public class WarningRecordRepository {
    private HibernateTemplate hibernateTemplate;
    private DriverManagerDataSource dataSource;

    public WarningRecordRepository(String jdbcDriver, String jdbcConnection, String jdbcUser, String jdbcPassword, String hibernateDialect) {
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot load specified JDBC driver: " + jdbcDriver, e);
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
            throw new RuntimeException("Could not set up database connection", e);
        }
        hibernateTemplate = new HibernateTemplate((SessionFactory) sessionFactoryBean.getObject());
    }

    public void recordWarningCount(WarningRecord record) {
        hibernateTemplate.save(record);
    }


    public Integer lastWarningCountFor(WarningRecord record) {
        DetachedCriteria c = DetachedCriteria.forClass(WarningRecord.class);
        ProjectVersion projectVersion = record.projectVersion();
        WarningSource source = record.source();
        c.add(Restrictions.eq("projectVersion.groupId", projectVersion.getGroupId()));
        c.add(Restrictions.eq("projectVersion.artifactId", projectVersion.getArtifactId()));
        c.add(Restrictions.eq("projectVersion.version", projectVersion.getVersion()));
        c.add(Restrictions.eq("source.source", source.getSource()));
        c.add(Restrictions.eq("source.metric", source.getMetric()));
        c.addOrder(Order.desc("dateLogged"));
        List<WarningRecord> matches = hibernateTemplate.findByCriteria(c, 0, 1);
        if (matches.isEmpty()) {
            return null;
        }
        return matches.get(0).warningCount();
    }
}
