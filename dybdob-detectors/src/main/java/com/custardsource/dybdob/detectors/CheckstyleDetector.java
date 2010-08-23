package com.custardsource.dybdob.detectors;

import com.google.common.collect.ImmutableMap;

public class CheckstyleDetector extends XmlParsingDetector {
    private static final String ALL_SELECTOR = "//error";
    private static final String ERROR_SELECTOR = "//error[@severity='error']";
    private static final String WARNING_SELECTOR = "//error[@severity='warning']";
    private static final String INFO_SELECTOR = "//error[@severity='info']";

    public CheckstyleDetector() {
        super("checkstyle", new DiffAlgorithm.XmlDiffAlgorithm("error"),
                ImmutableMap.of(
                        "all", ALL_SELECTOR, "error", ERROR_SELECTOR, 
                        "warning", WARNING_SELECTOR, "info", INFO_SELECTOR));
    }
}
