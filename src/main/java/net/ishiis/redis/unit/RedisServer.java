package net.ishiis.redis.unit;


import net.ishiis.redis.unit.config.RedisConfig;
import net.ishiis.redis.unit.config.RedisServerConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.ishiis.redis.unit.config.RedisServerConfig.DEFAULT_REDIS_SERVER_PORT;

/**
 * Redis server
 */
public class RedisServer implements Redis {
    private static final String REDIS_SERVER_START_UP_COMPLETE_MESSAGE = "The server is now ready to accept connections on port";
    private static final String REDIS_ADDRESS_ALREADY_IN_USE_MESSAGE = "Address already in use";
    private static final String REDIS_SENTINEL_START_UP_COMPLETE_MESSAGE = "Sentinel ID is";
    private static final String REDIS_GLIBC_NOT_FOUND = "GLIBC";

    private RedisConfig config;
    private Process process;

    /**
     * Default constructor
     */
    public RedisServer() {
        this(DEFAULT_REDIS_SERVER_PORT);
    }

    /**
     * Constructor
     * @param port server port
     */
    public RedisServer(Integer port) {
        this(new RedisServerConfig.ServerBuilder(port).build());
    }

    /**
     * Constructor
     * @param config {@link RedisConfig} list
     */
    public RedisServer(RedisConfig config) {
        this.config = config;
    }

    /**
     * Start redis server
     * @throws RuntimeException when failed to start redis server or failed to create resources
     */
    @Override
    public void start() {
        // Create Redis working directory and empty config file.
        Path tempDirectoryPath = config.getWorkingDirectory();
        Path logFilePath = Paths.get(tempDirectoryPath.toString(), config.getLogFile().toString());
        try {
            tempDirectoryPath.toFile().mkdirs();
            File configFile = Paths.get(tempDirectoryPath.toString(), config.getConfigFile().toString()).toFile();
            if (configFile.exists()){
                configFile.delete();
            }
            configFile.createNewFile();
            logFilePath.toFile().createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create a resource.", e);
        }

        Path redisBinary = Paths.get(config.getRedisBinaryPath());
        redisBinary.toFile().setExecutable(true);

        // Start redis process.
        ProcessBuilder processBuilder = new ProcessBuilder(config.getCommand());
        processBuilder.directory(tempDirectoryPath.toFile());
        processBuilder.redirectOutput(logFilePath.toFile());
        processBuilder.redirectError(logFilePath.toFile());

        try {
            process = processBuilder.start();
            try {Thread.sleep(1000L);} catch (InterruptedException e) {e.printStackTrace();}
        } catch (IOException e) {
            throw new RuntimeException("Unable to start Redis.", e);
        }

        try (BufferedReader bufferedReader = Files.newBufferedReader(logFilePath)) {
            String outputLine;
            do {
                outputLine = bufferedReader.readLine();
                if (outputLine == null) {
                    throw new RuntimeException("Output line does not exist.");
                }
                if (outputLine.contains(REDIS_GLIBC_NOT_FOUND)) {
                    throw new RuntimeException("GLIBC not found.");
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

    /**
     * Stop redis server
     * @throws RuntimeException when failed to stop redis server
     */
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

    /**
     * Return whether cluster is alive
     * @return {@code true} if  redis server is active, {@code false} otherwise
     */
    @Override
    public Boolean isActive() {
        return process != null && process.isAlive();
    }

}
