package com.custardsource.dybdob.detectors;

import org.testng.annotations.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Test
public class FindBugsDetectorTest {
    private static final File FINDBUGS_XML = new File(ClassLoader.getSystemClassLoader().getResource("findbugsXml.xml").getPath());
    private static final File FINDSECBUGS_XML = new File(ClassLoader.getSystemClassLoader().getResource("findsecbugsXml.xml").getPath());
    private static final String FINDBUGS_DETECTOR_IDENTIFIER = "findbugs";
    private static final String FINDSECBUGS_DETECTOR_IDENTIFIER = "findsecbugs";
    private static final File FINDBUGS_XML_NO_HIGH_PRIORITY = new File(ClassLoader.getSystemClassLoader().getResource("findbugsXmlNoHighPriority.xml").getPath());

    public void shouldFind37BugsInTotal() {
        assertThat(new FindBugsDetector(FINDBUGS_DETECTOR_IDENTIFIER).getResultsFrom(FINDBUGS_XML).get("all"), is(37));
    }

    public void shouldFind3HighPriorityBugs() {
        assertThat(new FindBugsDetector(FINDBUGS_DETECTOR_IDENTIFIER).getResultsFrom(FINDBUGS_XML).get("high"), is(3));
    }

    public void shouldFind0HighPriorityBugsWhenNoSuchAttributeExists() {
        assertThat(new FindBugsDetector(FINDBUGS_DETECTOR_IDENTIFIER).getResultsFrom(FINDBUGS_XML_NO_HIGH_PRIORITY).get("high"), is(0));
    }


    public void shouldFind11NormalPriorityBugs() {
        assertThat(new FindBugsDetector(FINDBUGS_DETECTOR_IDENTIFIER).getResultsFrom(FINDBUGS_XML).get("normal"), is(11));
    }

    public void shouldFind23LowPriorityBugs() {
        assertThat(new FindBugsDetector(FINDBUGS_DETECTOR_IDENTIFIER).getResultsFrom(FINDBUGS_XML).get("low"), is(23));
    }

    public void shouldFind9SecurityBugsInTotal() {
        assertThat(new FindBugsDetector(FINDSECBUGS_DETECTOR_IDENTIFIER).getResultsFrom(FINDSECBUGS_XML).get("all"), is(9));
    }

    public void shouldFind6HighPrioritySecurityBugs() {
        assertThat(new FindBugsDetector(FINDSECBUGS_DETECTOR_IDENTIFIER).getResultsFrom(FINDSECBUGS_XML).get("high"), is(0));
    }

    public void shouldFind6NormalPrioritySecurityBugs() {
        assertThat(new FindBugsDetector(FINDSECBUGS_DETECTOR_IDENTIFIER).getResultsFrom(FINDSECBUGS_XML).get("normal"), is(6));
    }

    public void shouldFind3LowPrioritySecurityBugs() {
        assertThat(new FindBugsDetector(FINDSECBUGS_DETECTOR_IDENTIFIER).getResultsFrom(FINDSECBUGS_XML).get("low"), is(3));
    }

}
