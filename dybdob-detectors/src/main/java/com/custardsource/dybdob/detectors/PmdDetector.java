package com.custardsource.dybdob.detectors;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

public class PmdDetector extends LineMatchingDetector {
    private static final Pattern ALL_PATTERN = Pattern.compile("<violation.*");
    private static final Pattern SEVERITY_5_PATTERN = Pattern.compile("<violation.*" + Pattern.quote("priority=\"5\"") + ".*");
    private static final Pattern SEVERITY_4_PATTERN = Pattern.compile("<violation.*" + Pattern.quote("priority=\"4\"") + ".*");
    private static final Pattern SEVERITY_3_PATTERN = Pattern.compile("<violation.*" + Pattern.quote("priority=\"3\"") + ".*");
    private static final Pattern SEVERITY_2_PATTERN = Pattern.compile("<violation.*" + Pattern.quote("priority=\"2\"") + ".*");
    private static final Pattern SEVERITY_1_PATTERN = Pattern.compile("<violation.*" + Pattern.quote("priority=\"1\"") + ".*");
    private static final Map<String, Pattern> PATTERNS = new HashMap<String, Pattern>();
    static {
        PATTERNS.put("all", ALL_PATTERN);
        PATTERNS.put("severity-1", SEVERITY_1_PATTERN);
        PATTERNS.put("severity-2", SEVERITY_2_PATTERN);
        PATTERNS.put("severity-3", SEVERITY_3_PATTERN);
        PATTERNS.put("severity-4", SEVERITY_4_PATTERN);
        PATTERNS.put("severity-5", SEVERITY_5_PATTERN);
    }

    public PmdDetector() {
        super("pmd", new DiffAlgorithm.XmlDiffAlgorithm(), ImmutableMap.copyOf(PATTERNS));
    }

}