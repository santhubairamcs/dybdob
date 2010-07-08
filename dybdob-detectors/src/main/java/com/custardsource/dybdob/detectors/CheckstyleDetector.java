package com.custardsource.dybdob.detectors;

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

public class CheckstyleDetector extends LineMatchingDetector {
    private static final Pattern ALL_PATTERN = Pattern.compile("<error.*");
    private static final Pattern ERROR_PATTERN = Pattern.compile("<error.*" + Pattern.quote("severity=\"error\"") + ".*");
    private static final Pattern WARNING_PATTERN = Pattern.compile("<error.*" + Pattern.quote("severity=\"warning\"") + ".*");
    private static final Pattern INFO_PATTERN = Pattern.compile("<error.*" + Pattern.quote("severity=\"info\"") + ".*");

    public CheckstyleDetector() {
        super("checkstyle", ImmutableMap.of("all", ALL_PATTERN, "error", ERROR_PATTERN, "warning", WARNING_PATTERN, "info", INFO_PATTERN));
    }
}
