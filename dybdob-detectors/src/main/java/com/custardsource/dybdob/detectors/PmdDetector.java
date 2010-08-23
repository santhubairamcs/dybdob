package com.custardsource.dybdob.detectors;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class PmdDetector extends XmlParsingDetector {
    private static final String ALL_SELECTOR = "//violation";
    private static final String SEVERITY_5_SELECTOR = "//violation[@priority='5']";
    private static final String SEVERITY_4_SELECTOR = "//violation[@priority='4']";
    private static final String SEVERITY_3_SELECTOR = "//violation[@priority='3']";
    private static final String SEVERITY_2_SELECTOR = "//violation[@priority='2']";
    private static final String SEVERITY_1_SELECTOR = "//violation[@priority='1']";
    private static final Map<String, String> SELECTORS = new HashMap<String, String>();
    static {
        SELECTORS.put("all", ALL_SELECTOR);
        SELECTORS.put("severity-1", SEVERITY_1_SELECTOR);
        SELECTORS.put("severity-2", SEVERITY_2_SELECTOR);
        SELECTORS.put("severity-3", SEVERITY_3_SELECTOR);
        SELECTORS.put("severity-4", SEVERITY_4_SELECTOR);
        SELECTORS.put("severity-5", SEVERITY_5_SELECTOR);
    }

    public PmdDetector() {
        super("pmd", new DiffAlgorithm.XmlDiffAlgorithm("violation"), ImmutableMap.copyOf(SELECTORS));
    }

}