package com.custardsource.dybdob.sonar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;

public class WarningSensor implements Sensor {
    @Override
    public void analyse(Project project, SensorContext context) {
        System.out.println(project.getFileSystem().getBuildDir());

        String foundWarnings;
        File input = new File(project.getFileSystem().getBuildDir(), "dybdob.warningcount");
        try {
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(input), "UTF-8"));
            foundWarnings = reader.readLine();
        } catch (FileNotFoundException e) {
            System.out.println("Warning count file dybdob.warningcount not found; not recording metric");
            return;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (StringUtils.isEmpty(foundWarnings)) {
            System.out.println("No warnings count found in file " + input + "; not recording metric");
            return;
        }
        double warningCount = Double.valueOf(foundWarnings);

        System.out.println("Warnings count: " + warningCount);

        context.saveMeasure(WarningMetrics.WARNINGS, warningCount);
    }

    @Override
    public boolean shouldExecuteOnProject(Project project) {
        return project.getPackaging().equals("jar");
    }
}
