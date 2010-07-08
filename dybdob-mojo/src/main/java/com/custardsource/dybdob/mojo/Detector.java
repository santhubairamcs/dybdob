package com.custardsource.dybdob.mojo;

import java.io.File;

public class Detector {
    private String id;
    private File logFile;

    public String id() {
        return id;
    }

    public File logFile() {
        return logFile;
    }

    @Override
    public String toString() {
        return "Detector{" +
                "id='" + id + '\'' +
                ", logFile=" + logFile +
                '}';
    }
}
