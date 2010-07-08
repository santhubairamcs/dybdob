package com.custardsource.dybdob.sonar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

public class WarningSensor implements Sensor {
    @Override
    public void analyse(Project project, SensorContext context) {
        String foundWarnings = null;
        File input = new File(project.getFileSystem().getBuildDir(), "dybdob.warnings");
        try {
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(input), "UTF-8"));
            String line = reader.readLine();
            if (line.startsWith("javac:warnings\t")) {
                foundWarnings = line.replace("javac:warnings\t", "");
            }
        } catch (FileNotFoundException e) {
            LoggerFactory.getLogger(WarningSensor.class).warn("Warning count file dybdob.warnings not found; not recording metric");
            return;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (StringUtils.isEmpty(foundWarnings)) {
            LoggerFactory.getLogger(WarningSensor.class).info("No javac.warnings count found in file {}; not recording metric", input);
            return;
        }
        double warningCount = Double.valueOf(foundWarnings);

        LoggerFactory.getLogger(WarningSensor.class).info("Warnings count = {}", warningCount);

        context.saveMeasure(WarningMetrics.WARNINGS, warningCount);
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return project.getFileSystem().hasJavaSourceFiles();
    }
}
