package com.custardsource.dybdob.sonar;

import java.util.Collections;
import java.util.List;

import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Metric;

public class WarningDecorator extends AbstractSumChildrenDecorator {
    @Override
    @DependedUpon
    public List<Metric> generatesMetrics() {
        return Collections.singletonList(WarningMetrics.WARNINGS);
    }

    @Override
    protected boolean shouldSaveZeroIfNoChildMeasures() {
        return false;
    }
}
