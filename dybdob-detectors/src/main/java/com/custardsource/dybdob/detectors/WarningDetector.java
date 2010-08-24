package com.custardsource.dybdob.detectors;

import java.io.File;
import java.util.Collection;

import com.custardsource.dybdob.ProjectVersion;
import com.custardsource.dybdob.WarningRecord;

public interface WarningDetector {
    public String getId();
    public Collection<WarningRecord> getRecords(ProjectVersion version, File log, File baseDir);

    public static class CountException extends RuntimeException {

        public CountException(String message) {
            super(message);
        }

        public CountException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
