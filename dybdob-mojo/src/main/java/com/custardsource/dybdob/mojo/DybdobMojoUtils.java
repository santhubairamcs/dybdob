package com.custardsource.dybdob.mojo;

import com.custardsource.dybdob.ProjectVersion;
import org.apache.maven.project.MavenProject;

public class DybdobMojoUtils {
    public static ProjectVersion buildProjectVersionFrom(MavenProject mavenProject) {
        return new ProjectVersion(mavenProject.getGroupId(), mavenProject.getArtifactId(), mavenProject.getVersion());
    }
}
