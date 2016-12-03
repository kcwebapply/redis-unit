package net.ishiis.redis.unit;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RedisServer implements Redis {
    public static final Integer DEFAULT_REDIS_SERVER_PORT = 6379;

    private static final String REDIS_SERVER_START_UP_COMPLETE_MESSAGE = "The server is now ready to accept connections on port";
    private static final String REDIS_ADDRESS_ALREADY_IN_USE_MESSAGE = "Address already in use";
    private static final String REDIS_SENTINEL_START_UP_COMPLETE_MESSAGE = "Sentinel ID is";

    private RedisConfig config;
    private Process process;

    public RedisServer() {
        this(DEFAULT_REDIS_SERVER_PORT);
    }

    public RedisServer(Integer port) {
        this(new RedisConfig.ServerBuilder(port).build());
    }

    public RedisServer(RedisConfig config) {
        this.config = config;
    }

    @Override
    public void start() {
        // Create Redis working directory and empty config file.
        Path tempDirectory;
        try {
            tempDirectory = Files.createTempDirectory("redis-unit-");
            Paths.get(tempDirectory.toString(), config.getConfigFile().toString()).toFile().createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create a temp file.", e);
        }

        Path redisBinary = Paths.get(config.getRedisBinaryPath());
        redisBinary.toFile().setExecutable(true);

        // Start redis server.
        ProcessBuilder processBuilder = new ProcessBuilder(config.getCommand());
        processBuilder.directory(tempDirectory.toFile());

        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start Redis.", e);
        }

        File logFile = Paths.get(tempDirectory.toString(), config.getLogFile().toString()).toFile();

        try (FileReader fileReader = new FileReader(logFile);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            try {Thread.sleep(1000L);} catch (InterruptedException e) {e.printStackTrace();}
            String outputLine;
            do {
                outputLine = bufferedReader.readLine();
                if (outputLine == null) {
                    throw new RuntimeException("Output line does not exist.");
                }
                if (outputLine.contains(REDIS_ADDRESS_ALREADY_IN_USE_MESSAGE)) {
                    throw new RuntimeException("Address already in use.");
                }
            } while (!outputLine.contains(REDIS_SERVER_START_UP_COMPLETE_MESSAGE) &&
                    !outputLine.contains(REDIS_SENTINEL_START_UP_COMPLETE_MESSAGE));
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
    public Boolean isActive() {
        return process != null && process.isAlive();
    }

}
