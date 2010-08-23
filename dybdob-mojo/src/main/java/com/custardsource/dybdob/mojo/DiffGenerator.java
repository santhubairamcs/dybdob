package com.custardsource.dybdob.mojo;

import java.util.List;

import com.custardsource.dybdob.WarningRecord;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import difflib.DiffUtils;
import difflib.Patch;

public class DiffGenerator {
    private static final int DIFF_CONTEXT_LINES = 5;
    private static final String DIFF_NEW_FILE_NAME = "new";
    private static final String DIFF_OLD_FILE_NAME = "old";

    private final WarningRecord oldRecord;
    private final WarningRecord newRecord;

    public DiffGenerator(WarningRecord oldRecord, WarningRecord newRecord) {
        this.oldRecord = oldRecord;
        this.newRecord = newRecord;
    }

    public String diff() {
        if (oldRecord == null || oldRecord.toolOutput() == null) {
            return newRecord.toolOutput();
        }

        String oldDiff = cleanUp(oldRecord);
        String newDiff = cleanUp(newRecord);

        Splitter splitter = Splitter.on(CharMatcher.anyOf("\r\n"));
        List<String> oldLines = Lists.newArrayList(splitter.split(oldDiff));
        List<String> newLines = Lists.newArrayList(splitter.split(newDiff));
        if (oldLines.equals(newLines)) {
            return "";
        }
        Patch diffs = DiffUtils.diff(oldLines, newLines);
        return Joiner.on("\n").join(DiffUtils.generateUnifiedDiff(DIFF_OLD_FILE_NAME, DIFF_NEW_FILE_NAME, oldLines,
                diffs, DIFF_CONTEXT_LINES));
    }

    private String cleanUp(WarningRecord record) {
        String result = record.toolOutput();
        String basePath = record.executionPath();
        if (basePath != "" && !basePath.isEmpty()) {
            result = result.replace(basePath, "<base>");
        }
        result = result.replace("\\", "/");
        return result;
    }
}
