package com.custardsource.dybdob;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;

public class JavacWarningDetector implements WarningDetector {
    private static final Pattern WARNING_PATTERN = Pattern.compile(".*" + Pattern.quote(": warning: [") + ".*");
    private static final WarningSource JAVAC_WARNINGS = new WarningSource("javac", "warnings");

    @Override
    public WarningRecord getRecords(ProjectVersion version, File compilerLog) {
        if (!compilerLog.exists()) {
            throw new CountException("Could not read file " + compilerLog);
        }

        try {
            int warningCount = Collections2.filter(Files.readLines(compilerLog, Charsets.UTF_8), new Predicate<String>() {
                @Override
                public boolean apply(String input) {
                    return WARNING_PATTERN.matcher(input).matches();
                }
            }).size();
            return WarningRecord.newRecord(version, JAVAC_WARNINGS, warningCount);
        } catch (IOException e) {
            throw new CountException("Could not parse file " + compilerLog, e);
        }
    }
}
