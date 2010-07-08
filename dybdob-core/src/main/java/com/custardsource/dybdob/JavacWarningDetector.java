package com.custardsource.dybdob;

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

public class JavacWarningDetector extends LineMatchingDetector {
    private static final Pattern WARNING_PATTERN = Pattern.compile(".*" + Pattern.quote(": warning: [") + ".*");

    public JavacWarningDetector() {
        super("javac", ImmutableMap.of("warnings", WARNING_PATTERN));
    }

}
