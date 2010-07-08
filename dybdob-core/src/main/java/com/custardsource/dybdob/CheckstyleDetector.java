package com.custardsource.dybdob;

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

public class CheckstyleDetector extends LineMatchingDetector {
    private static final Pattern ALL_ERRORS_PATTERN = Pattern.compile("<error.*");

    public CheckstyleDetector() {
        super("checkstyle", ImmutableMap.of("all", ALL_ERRORS_PATTERN));
    }
}
