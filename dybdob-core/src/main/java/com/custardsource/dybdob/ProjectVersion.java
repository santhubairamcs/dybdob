package com.custardsource.dybdob;

import javax.persistence.Embeddable;

@Embeddable
public class ProjectVersion {
    private String groupId;

    private String artifactId;

    private String version;

    ProjectVersion() {
    }
    
    public ProjectVersion(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }
}
