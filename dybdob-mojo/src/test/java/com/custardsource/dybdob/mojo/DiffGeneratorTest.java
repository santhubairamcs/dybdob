package com.custardsource.dybdob.mojo;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.custardsource.dybdob.WarningRecord;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DiffGeneratorTest {
    @Mock
    WarningRecord oldRecord;

    @Mock
    WarningRecord newRecord;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void diffWithNoOldRecordShouldReturnNewRecordContents() {
        String sample = "Foo\nBar";
        when(newRecord.toolOutput()).thenReturn(sample);
        String result = new DiffGenerator(null, newRecord).diff();
        assertThat(result, is(sample));
    }

    @Test
    public void diffWithNoOldRecordOutputShouldReturnNewRecordContents() {
        String sample = "Foo\nBar";
        when(newRecord.toolOutput()).thenReturn(sample);
        String result = new DiffGenerator(oldRecord, newRecord).diff();
        assertThat(result, is(sample));
    }

    @Test
    public void diffShouldIgnoreFilePathDiffers() {
        when(oldRecord.toolOutput()).thenReturn("<file='/tmp/foo/2'>");
        when(oldRecord.executionPath()).thenReturn("/tmp");
        when(newRecord.toolOutput()).thenReturn("<file='/var/tmp/foo/2'>");
        when(newRecord.executionPath()).thenReturn("/var/tmp");
        String result = new DiffGenerator(oldRecord, newRecord).diff();
        assertThat(result, is(""));
    }

    @Test
    public void diffShouldIgnorePathSeparators() {
        when(oldRecord.toolOutput()).thenReturn("<file='/tmp/foo/2'>");
        when(oldRecord.executionPath()).thenReturn("/tmp");
        when(newRecord.toolOutput()).thenReturn("<file='C:\\temp\\foo\\2'>");
        when(newRecord.executionPath()).thenReturn("C:\\temp");
        String result = new DiffGenerator(oldRecord, newRecord).diff();
        assertThat(result, is(""));
    }

}
