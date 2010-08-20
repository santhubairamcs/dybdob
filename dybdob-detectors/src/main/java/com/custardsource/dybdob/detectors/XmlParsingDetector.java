package com.custardsource.dybdob.detectors;

import java.util.Map;
import java.util.regex.Pattern;

public abstract class XmlParsingDetector extends AbstractDetector {
    private final Map<String, String> metricXPathPatterns;

    protected XmlParsingDetector(String detectorName, Map<String, String> metricXPathPatterns) {
        super(detectorName);
        this.metricXPathPatterns= metricXPathPatterns;
    }

}
