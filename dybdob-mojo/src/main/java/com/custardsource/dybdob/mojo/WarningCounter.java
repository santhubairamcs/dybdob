package com.custardsource.dybdob.mojo;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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

        try {
            return Collections2.filter(Files.readLines(compilerLog, Charsets.UTF_8), new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return WARNING_PATTERN.matcher(input).matches();
                }
            }).size();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not parse file " + compilerLog, e);
        }
    }
}
