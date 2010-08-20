package com.custardsource.dybdob.detectors;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.custardsource.dybdob.ProjectVersion;
import com.custardsource.dybdob.WarningRecord;
import com.custardsource.dybdob.WarningSource;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;

abstract class AbstractDetector implements WarningDetector {
    protected final String id;

    protected AbstractDetector(String id) {
        this.id = id;
    }

    @Override
    public final Collection<WarningRecord> getRecords(final ProjectVersion version, final File log) {
        Map<String, Integer> results = getResultsFrom(log);
        final String rawOutput = readOutputFrom(log);
        return Collections2.transform(results.entrySet(), new Function<Map.Entry<String, Integer>, WarningRecord>(){
            @Override
            public WarningRecord apply(Map.Entry<String, Integer> from) {
                return WarningRecord.newRecord(version, new WarningSource(id, from.getKey()), from.getValue(), rawOutput);
            }
        });
    }

    protected String readOutputFrom(File log) {
        try {
            return Files.toString(log, Charsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    public String getId() {
        return id;
    }

    protected abstract Map<String, Integer> getResultsFrom(File log);
}
