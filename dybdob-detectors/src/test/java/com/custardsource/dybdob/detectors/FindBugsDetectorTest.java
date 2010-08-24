package com.custardsource.dybdob.detectors;

import org.testng.annotations.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Test
public class FindBugsDetectorTest {
    private static final File FINDBUGS_XML = new File(ClassLoader.getSystemClassLoader().getResource("findbugsXml.xml").getPath());

    public void shouldFind37BugsInTotal() {
        assertThat(new FindBugsDetector().getResultsFrom(FINDBUGS_XML).get("all"), is(37));
    }

    public void shouldFind3HighPriorityBugs() {
        assertThat(new FindBugsDetector().getResultsFrom(FINDBUGS_XML).get("high"), is(3));
    }

    public void shouldFind11NormalPriorityBugs() {
        assertThat(new FindBugsDetector().getResultsFrom(FINDBUGS_XML).get("normal"), is(11));
    }

    public void shouldFind23LowPriorityBugs() {
        assertThat(new FindBugsDetector().getResultsFrom(FINDBUGS_XML).get("low"), is(23));
    }

}
