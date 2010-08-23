package com.custardsource.dybdob.detectors;

import com.google.common.collect.ImmutableMap;

public class CpdDetector extends XmlParsingDetector {
    private static final String DUPLICATION_SELECTOR = "//duplication";

    public CpdDetector() {
        super("cpd", new DiffAlgorithm.XmlDiffAlgorithm(), ImmutableMap.of("duplication", DUPLICATION_SELECTOR));
    }
}