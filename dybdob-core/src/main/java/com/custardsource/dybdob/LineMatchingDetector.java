package com.custardsource.dybdob;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

class LineMatchingDetector extends AbstractDetector {
    private final Map<String, Pattern> metricMatchers;

    protected LineMatchingDetector(String detectorName, Map<String, Pattern> metricMatchers) {
        super(detectorName);
        this.metricMatchers = metricMatchers;
    }

    @Override
    protected Map<String, Integer> getResultsFrom(File compilerLog) {
        if (!compilerLog.exists() || !compilerLog.canRead()) {
            throw new CountException("Could not read file " + compilerLog);
        }

        try {
            Map<String, Integer> results = new HashMap<String, Integer>();
            for (String metric : metricMatchers.keySet()) {
                results.put(metric, 0);
            }
            
            for (String line : Files.readLines(compilerLog, Charsets.UTF_8)) {
                for (Map.Entry<String, Pattern> matcher : metricMatchers.entrySet()) {
                    if (matcher.getValue().matcher(line).matches()) {
                        results.put(matcher.getKey(), results.get(matcher.getKey()) + 1);
                    }
                }
            }

            return results;
        } catch (IOException e) {
            throw new CountException("Could not parse file " + compilerLog, e);
        }
    }

}
