package com.custardsource.dybdob.mojo;

import java.io.File;
import java.util.Collections;
import java.util.Set;

public class Detector {
    private String id;
    private File logFile;
    private Set<String> enabledChecks = Collections.emptySet();

    public String id() {
        return id;
    }

    public File logFile() {
        return logFile;
    }

    public boolean isCheckEnabled(String key) {
        return enabledChecks.isEmpty() || enabledChecks.contains(key);
    }

    @Override
    public String toString() {
        return "Detector{" +
                "id='" + id + '\'' +
                ", logFile=" + logFile +
                ", enabledChecks=" + enabledChecks + "}";
    }
}
