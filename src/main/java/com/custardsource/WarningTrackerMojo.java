package com.custardsource;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Goal which retrieves a compiler warning count
 *
 * @goal checkcount
 * @phase compile
 */
public class WarningTrackerMojo
        extends AbstractMojo {
    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}/javac.out"
     * @required
     */
    private File outputDirectory;

    private final Pattern pattern = Pattern.compile(".*" + Pattern.quote("warning: [") + ".*");

    public void execute()
            throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            throw new MojoExecutionException("Could not read file " + outputDirectory);
        }

        int warningCount = 0;
        try {
            for (String line : Files.readLines(outputDirectory, Charsets.UTF_8)) {

                if (pattern.matcher(line).matches()) {
                    warningCount++;
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not parse file " + outputDirectory, e);
        }

        if (warningCount > 0) {
            throw new MojoExecutionException(String.format("Failing build with warning count %s, no warnings permitted; see %s for warning details", warningCount, outputDirectory));
        }
    }
}
