package com.custardsource.dybdob.mojo;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.maven.project.MavenProject;
import org.hibernate.annotations.Index;

@Entity
@org.hibernate.annotations.Table(
        appliesTo="WarningRecord",
    indexes = { @Index(name="idx_WarningRecord", columnNames = { "groupId", "artifactId", "version", "dateLogged" } ) }
    )
public class WarningRecord {
    @Id
    private String id;

    private String groupId;

    private String artifactId;

    private String version;

    private Date dateLogged;

    private int warningCount;

    WarningRecord() {
    }

    static WarningRecord newRecord(MavenProject project, int warningCount) {
        WarningRecord record = new WarningRecord();
        record.groupId = project.getGroupId();
        record.artifactId = project.getArtifactId();
        record.version = project.getVersion();
        record.dateLogged = new Date();
        record.warningCount = warningCount;
        record.id = UUID.randomUUID() + "-" + System.nanoTime();
        return record;
    }

    public int warningCount() {
        return warningCount;
    }
}
