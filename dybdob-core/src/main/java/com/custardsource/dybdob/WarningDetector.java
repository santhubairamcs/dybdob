package com.custardsource.dybdob;

import java.io.File;
import java.util.Collection;

public interface WarningDetector {
    public Collection<WarningRecord> getRecords(ProjectVersion version, File log);

    public static class CountException extends RuntimeException {

        public CountException(String message) {
            super(message);
        }

        public CountException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
