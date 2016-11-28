package net.ishiis.redis.unit;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RedisServer extends Redis {
    public static final Integer DEFAULT_REDIS_SERVER_PORT = 6379;

    private static final String REDIS_START_UP_COMPLETE_MESSAGE = "The server is now ready to accept connections on port";
    private static final String REDIS_ADDRESS_ALREADY_IN_USE = "Address already in use";

    private List<String> command;
    private Process process;
    private Integer port;
    private String logFileName;

    public RedisServer() {
        this(DEFAULT_REDIS_SERVER_PORT);
    }

    public RedisServer(Integer port) {
        this.port = port;
        this.logFileName = port + "_" + UUID.randomUUID().toString() + ".log";
        this.command = Arrays.asList(getDefaultRedisBinaryPath(),
                "--port", Integer.toString(port), "--logfile", logFileName);
    }

    public RedisServer(String redisBinaryPath) {
        this(redisBinaryPath, DEFAULT_REDIS_SERVER_PORT);
    }

    public RedisServer(String redisBinaryPath, Integer port) {
        this.port = port;
        this.command = Arrays.asList(redisBinaryPath, "--port", Integer.toString(port));
    }

    public RedisServer(List<String> command) {
        this.command = command;
    }

    @Override
    public void start() {
        // Create Redis working directory.
        File tempDirectory;
        try {
            tempDirectory = Files.createTempDirectory("redis-unit").toFile();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create a directory.", e);
        }

        File redisBinary = new File(command.get(0));
        redisBinary.setExecutable(true);

        // Start redis server.
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(tempDirectory);
        try {
            process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start Redis.", e);
        }

        File logFile = new File(tempDirectory.getPath(), logFileName);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile))) {
            String outputLine;
            do {
                outputLine = bufferedReader.readLine();
                if (outputLine == null) {
                    throw new RuntimeException("Output line does not exist.");
                }
                if (outputLine.contains(REDIS_ADDRESS_ALREADY_IN_USE)) {
                    throw new RuntimeException("Address already in use.");
                }
            } while (!outputLine.contains(REDIS_START_UP_COMPLETE_MESSAGE));
        } catch (IOException e) {
            throw new RuntimeException("IOException: ", e);
        }
    }

    @Override
    public void stop() {
        process.destroy();
        if (process.isAlive()){
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException("Can not stop Redis process.");
            }
        }
    }

    @Override
    public Boolean isAlive() {
        return process != null && process.isAlive();
    }

    public Integer getPort() {
        return port;
    }

}
