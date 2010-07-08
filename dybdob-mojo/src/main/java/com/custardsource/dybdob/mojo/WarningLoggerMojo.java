package com.custardsource.dybdob.mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.custardsource.dybdob.WarningRecord;
import com.custardsource.dybdob.detectors.WarningDetector;
import com.google.common.base.Charsets;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which fails the build if a compiler log has indicated any warnings
 *
 * @goal logwarnings
 * @phase verify
 */
public class WarningLoggerMojo extends DybdobMojo {
    /**
     * File to write warning counts to
     *
     * @parameter expression="${project.build.directory}/dybdob.warnings"
     * @required
     */
    private File outputFile;

    private Writer writer;

    @Override
    protected void checkSingleRecord(WarningRecord record, File logFile, WarningDetector warningDetector) throws MojoExecutionException {
        writeWarningCountToLogFile(record);
        getLog().info(String.format("Warnings found for metric %s: %s", record.source(), record.warningCount()));
    }

    private void writeWarningCountToLogFile(WarningRecord record) throws MojoExecutionException {
        try {
            writer.write(record.source() + "\t" + record.warningCount() + "\n");
        } catch (IOException e) {
            throw new MojoExecutionException("Could not pass on warning count to subsequent plugins", e);
        }
    }


    @Override
    protected void initialize() throws MojoExecutionException {
        try {
            writer = new OutputStreamWriter(new FileOutputStream(outputFile, false), Charsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException("Cannot write to file " + outputFile, e);
        }
    }

    @Override
    protected void tearDown() {
        try {
            writer.close();
        } catch (IOException e) {
            getLog().error("Cannot close writer " + outputFile, e);
        }
    }
}