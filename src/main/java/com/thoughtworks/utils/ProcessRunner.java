package com.thoughtworks.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.getProperty;

public class ProcessRunner {

    public static final ProcessRunner SVN = new ProcessRunner("svn");
    private final String executable;

    private ProcessRunner(String executable) {
        this.executable = executable;
    }

    public List<String> execute(String arguments) {
        return execute(arguments.split("\\s+"));
    }
    public List<String> execute(String... arguments) {
        final CommandLine commandLine = new CommandLine(executable).addArguments(arguments);
        final String command = String.join(" ", commandLine.toStrings());
        System.out.println("Executing: " + command);
        Executor executor = new DefaultExecutor();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            executor.setStreamHandler(new PumpStreamHandler(out));
            try {
                int result = executor.execute(commandLine);
                if (result != 0) {
                    throw new IllegalStateException("Failed to execute command: " + command + "' successfully.");
                }
                return asList(out.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to execute command: '" + command + "'", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to open stream: '" + command + "'", e);
        }

    }

    private List<String> asList(String output) {
        return new ArrayList<>(Arrays.asList(output.split(getProperty("line.separator"))));
    }
}
