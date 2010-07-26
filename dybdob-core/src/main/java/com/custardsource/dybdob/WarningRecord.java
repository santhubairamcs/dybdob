package com.custardsource.dybdob;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Index;

@Entity
@org.hibernate.annotations.Table(
        appliesTo="WarningRecord",
        /* Note: this should result in an index being created on these columns, but due to a bug in Hibernate
         * (http://opensource.atlassian.com/projects/hibernate/browse/HHH-1012) this doesn't happen on auto-schema
         * update. Need to create the equivalent index manually on any table which is going to grow substantially.
         * Example Transact-SQL:
         *   CREATE INDEX idx_warningrecord ON WarningRecord(groupId, artifactId, version, source, qualifier, dateLogged)
         */
        indexes = { @Index(name="idx_WarningRecord", columnNames = { "groupId", "artifactId", "version", "source", "metric", "dateLogged" } ) }
    )
@SuppressWarnings("unused")
public class WarningRecord {
    @Id
    private String id;

    @Embedded
    private ProjectVersion projectVersion;

    private Date dateLogged;

    private int warningCount;

    @Embedded
    private WarningSource source;

    @Lob
    private String toolOutput;

    WarningRecord() {
    }

    public static WarningRecord newRecord(ProjectVersion projectVersion, WarningSource source, int warningCount, String toolOutput) {
        WarningRecord record = new WarningRecord();
        record.projectVersion = projectVersion;
        record.dateLogged = new Date();
        record.warningCount = warningCount;
        record.source = source;
        record.id = UUID.randomUUID() + "-" + System.nanoTime();
        record.toolOutput = toolOutput;
        return record;
    }

    public int warningCount() {
        return warningCount;
    }

    public WarningSource source() {
        return source;
    }

    public ProjectVersion projectVersion() {
        return projectVersion;
    }

    public String toolOutput() {
        return toolOutput;
    }
    
    @Override
    public String toString() {
        return source + ":" + warningCount;
    }
}
