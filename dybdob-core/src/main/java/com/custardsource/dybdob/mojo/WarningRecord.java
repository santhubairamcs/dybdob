package com.custardsource.dybdob.mojo;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Index;

@Entity
@org.hibernate.annotations.Table(
        appliesTo="WarningRecord",
    indexes = { @Index(name="idx_WarningRecord", columnNames = { "groupId", "artifactId", "version", "dateLogged" } ) }
    )
public class WarningRecord {
    @Id
    private String id;

    @Embedded
    private ProjectVersion projectVersion;

    private Date dateLogged;

    private int warningCount;

    WarningRecord() {
    }

    static WarningRecord newRecord(ProjectVersion projectVersion, int warningCount) {
        WarningRecord record = new WarningRecord();
        record.projectVersion = projectVersion;
        record.dateLogged = new Date();
        record.warningCount = warningCount;
        record.id = UUID.randomUUID() + "-" + System.nanoTime();
        return record;
    }

    public int warningCount() {
        return warningCount;
    }
}
