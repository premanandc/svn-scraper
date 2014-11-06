package com.thoughtworks.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.getProperty;

public class ProcessRunner {

    public static final ProcessRunner SVN = new ProcessRunner("svn");
    private final String executable;
    private static final Logger logger = LoggerFactory.getLogger(ProcessRunner.class);

    public ProcessRunner(String executable) {
        this.executable = executable;
    }

    public List<String> execute(String arguments) {
        return execute(arguments.split("\\s+"));
    }
    public List<String> execute(String... arguments) {
        try (OutputStream out = new ByteArrayOutputStream()) {
            return asList(out, arguments);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open stream", e);
        }

    }

    private List<String> asList(OutputStream out, String[] arguments) {
        final CommandLine commandLine = new CommandLine(executable).addArguments(arguments);
        final String command = String.join(" ", commandLine.toStrings());
        logger.info("Executing " + command);
        Executor executor = new DefaultExecutor();
        executor.setExitValues(null);
        executor.setStreamHandler(new PumpStreamHandler(out));
        try {
            executor.execute(commandLine);
            return asList(out.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute command: '" + command + "'", e);
        }
    }

    private List<String> asList(String output) {
        return new ArrayList<>(Arrays.asList(output.split(getProperty("line.separator"))));
    }
}
