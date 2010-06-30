package com.custardsource.dybdob.mojo;

import javax.persistence.Embeddable;

@Embeddable
public class ProjectVersion {
    private String groupId;

    private String artifactId;

    private String version;

    public ProjectVersion(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }
}
