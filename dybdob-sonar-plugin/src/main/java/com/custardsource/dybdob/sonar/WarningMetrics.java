package com.custardsource.dybdob.sonar;

import java.util.Collections;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public class WarningMetrics implements Metrics {
    public static final Metric WARNINGS = new Metric("warnings", "Compiler warnings", "Metric to keep track of compiler warnings", Metric.ValueType.INT, Metric.DIRECTION_WORST, false, CoreMetrics.DOMAIN_GENERAL);

    @Override
    public List<Metric> getMetrics() {
        return Collections.singletonList(WARNINGS);
    }
}
