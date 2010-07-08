package com.custardsource.dybdob;

import javax.persistence.Embeddable;

@SuppressWarnings({"FieldCanBeLocal"})
@Embeddable
public class WarningSource {
    private String source;
    private String metric;

    WarningSource() {
    }

    public WarningSource(String source, String metric) {
        this.source = source;
        this.metric = metric;
    }

    public String getSource() {
        return source;
    }

    public String getMetric() {
        return metric;
    }

    @Override
    public String toString() {
        return source + ':' + metric;
    }
}
