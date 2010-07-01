package com.custardsource.dybdob.sonar;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.Extension;
import org.sonar.api.Plugin;

public class DybdobPlugin implements Plugin {
    @Override
    public String getKey() {
        return "dybdobWarnings";
    }

    @Override
    public String getName() {
        return "dybdob Warnings Plugin";
    }

    @Override
    public String getDescription() {
        return "Tracks javac compiler warnings over time";
    }

    @Override
    public List<Class<? extends Extension>> getExtensions() {
        List<Class<? extends Extension>> extensions = new ArrayList<Class<? extends Extension>>();
        extensions.add(WarningMetrics.class);
        extensions.add(WarningSensor.class);
        extensions.add(WarningDashboardWidget.class);
        return extensions;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
