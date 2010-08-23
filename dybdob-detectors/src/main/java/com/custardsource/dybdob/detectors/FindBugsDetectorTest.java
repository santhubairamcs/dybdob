package com.custardsource.dybdob.detectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;

import com.google.common.io.CharStreams;
import org.testng.annotations.Test;

@Test
public class FindBugsDetectorTest {
    public void shouldReportZeroWarningsForEmptyFile() {
        assertEquals(warningCountFor("findbugs-empty.xml", "all"), 0);
    }

    public void shouldReportCorrectWarningCountForBugFile() {
        assertEquals(warningCountFor("findbugs-base.xml", "all"), 2);
    }

    public void shouldNotReportDiffsWhenLineNumbersChange() {
        String diff = diffBetween("findbugs-base.xml", "findbugs-linenumberschanged.xml");
        assertEquals(diff, "");
    }

    public void shouldNotReportDiffsWhenErrorsRemoved() {
        String diff = diffBetween("findbugs-base.xml", "findbugs-empty.xml");
        assertEquals(diff, "");
    }

    public void shouldReportDiffForNewWarning() {
        String diff = diffBetween("findbugs-base.xml", "findbugs-newwarning.xml");
        assertTrue(diff.contains("DMI_RANDOM_USED_ONLY_ONCE"));
    }

    public void shouldReportDiffWhenSameLineReplaced() {
        String diff = diffBetween("findbugs-base.xml", "findbugs-warningreplaced.xml");
        assertTrue(diff.contains("DMI_RANDOM_USED_ONLY_ONCE"));
    }

    public void shouldReportDiffWhenNewFileInserted() {
        String diff = diffBetween("findbugs-base.xml", "findbugs-newfile.xml");
        System.out.println("diff = " + diff);
        assertTrue(diff.contains("DMI_RANDOM_USED_ONLY_ONCE"));
        assertFalse(diff.contains("DLS_DEAD_LOCAL_STORE"));
    }

    private String diffBetween(String basePath, String modifiedPath) {
        return new FindBugsDetector().getDiffAlgorithm().diff(asString(basePath), asString(modifiedPath));
    }

    private String asString(String path) {
        try {
            return CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("samplefiles/" + path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int warningCountFor(String path, String warning) {
        
        return new FindBugsDetector().getResultsFrom(getClass().getResourceAsStream("samplefiles/" + path)).get(warning);
    }
}
