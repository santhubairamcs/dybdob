package com.custardsource.dybdob.detectors;

import java.io.File;

import com.google.common.collect.ImmutableMap;

public class FindBugsDetector extends XmlParsingDetector {
    private static final String ALL_SELECTOR = "//BugInstance";
    private static final String HIGH_SELECTOR = "//BugInstance[@priority='High']";
    private static final String NORMAL_SELECTOR = "//BugInstance[@priority='Normal']";
    private static final String LOW_SELECTOR = "//BugInstance[@priority='Low']";

    public FindBugsDetector() {
        super("findbugs", new DiffAlgorithm.XmlDiffAlgorithm("BugInstance"), ImmutableMap.of(
                "all", ALL_SELECTOR,
                "high", HIGH_SELECTOR,
                "normal", NORMAL_SELECTOR,
                "low", LOW_SELECTOR
        ));
    }

    @Override
    protected String readOutputFrom(File log) {
        return super.readOutputFrom(log).replaceAll("<", "\n<");
    }
}