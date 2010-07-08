package com.custardsource.dybdob;

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

public class CpdDetector extends LineMatchingDetector {
    private static final Pattern DUPLICATION_PATTERN = Pattern.compile("<duplication.*");

    public CpdDetector() {
        super("cpd", ImmutableMap.of("duplication", DUPLICATION_PATTERN));
    }
}