package com.custardsource;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class WarningCounter {
    private static final Pattern WARNING_PATTERN = Pattern.compile(".*" + Pattern.quote("warning: [") + ".*");

    private final File compilerLog;

    public WarningCounter(File compilerLog) {
        this.compilerLog = compilerLog;
    }

    public int warningCount() throws MojoExecutionException {
        if (!compilerLog.exists()) {
            throw new MojoExecutionException("Could not read file " + compilerLog);
        }

        int warningCount = 0;
        try {
            for (String line : Files.readLines(compilerLog, Charsets.UTF_8)) {

                if (WARNING_PATTERN.matcher(line).matches()) {
                    warningCount++;
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Could not parse file " + compilerLog, e);
        }
        return warningCount;
    }
}
