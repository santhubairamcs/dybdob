package com.custardsource.dybdob;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;

public class WarningCounter {
    private static final Pattern WARNING_PATTERN = Pattern.compile(".*" + Pattern.quote(": warning: [") + ".*");

    private final File compilerLog;

    public WarningCounter(File compilerLog) {
        this.compilerLog = compilerLog;
    }

    public int warningCount() throws CountException {
        if (!compilerLog.exists()) {
            throw new CountException("Could not read file " + compilerLog);
        }

        try {
            return Collections2.filter(Files.readLines(compilerLog, Charsets.UTF_8), new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return WARNING_PATTERN.matcher(input).matches();
                }
            }).size();
        } catch (IOException e) {
            throw new CountException("Could not parse file " + compilerLog, e);
        }
    }

    public static class CountException extends RuntimeException {

        public CountException(String message) {
            super(message);
        }

        public CountException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
