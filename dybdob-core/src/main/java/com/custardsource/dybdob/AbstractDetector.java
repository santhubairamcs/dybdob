package com.custardsource.dybdob;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

abstract class AbstractDetector implements WarningDetector {
    private final String id;

    protected AbstractDetector(String id) {
        this.id = id;
    }

    @Override
    public final Collection<WarningRecord> getRecords(final ProjectVersion version, File log) {
        Map<String, Integer> results = getResultsFrom(log);
        return Collections2.transform(results.entrySet(), new Function<Map.Entry<String, Integer>, WarningRecord>(){
            @Override
            public WarningRecord apply(Map.Entry<String, Integer> from) {
                return WarningRecord.newRecord(version, new WarningSource(id, from.getKey()), from.getValue());
            }
        });
    }

    @Override
    public String getId() {
        return id;
    }

    protected abstract Map<String, Integer> getResultsFrom(File log);
}
