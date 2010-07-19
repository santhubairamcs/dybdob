package com.custardsource.dybdob.mojo.utils;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal to create a directory 
 *
 * @goal createdirectory
 */
public class DirectoryCreatorMojo extends AbstractMojo {
    /**
     * Folder to create
     *
     * @parameter
     * @required
     */
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (outputDirectory.isFile()) {
            throw new MojoExecutionException("Cannot create directory over the top of existing file " + outputDirectory);
        }
        if (outputDirectory.isDirectory()) {
            getLog().info("No need to create directory " + outputDirectory + ", already exists");
        }
        if (!outputDirectory.mkdirs()) {
            throw new MojoExecutionException("Cannot create directory " + outputDirectory + "; unknown error");
        }
        getLog().info("Successfully created directory " + outputDirectory);
    }
}
