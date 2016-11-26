package net.ishiis;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class RedisServer implements Redis {
    private static final String REDIS_BINARY_NAME = "redis-server.3.2.5";
    private static final Integer DEFAULT_REDIS_PORT = 6379;

    private List<String> command;

    private Process redisProcess;

    public RedisServer() {
        this(DEFAULT_REDIS_PORT);
    }

    public RedisServer(Integer port) {
        this.command = Arrays.asList(REDIS_BINARY_NAME, "--port", Integer.toString(port));
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

        // Set Redis binary path.
        String redisBinaryPath = null;
        try {
            redisBinaryPath = URLDecoder.decode(
                    this.getClass().getClassLoader().getResource(command.get(0)).getPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is not supported Encoding.", e);
        }

        File redisBinary = new File(redisBinaryPath);
        redisBinary.setExecutable(true);

        // Start redis server.
        ProcessBuilder pb = new ProcessBuilder(redisBinaryPath);
        pb.directory(tempDirectory);
        try {
            redisProcess = pb.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start Redis.", e);
        }
    }

    @Override
    public void stop() {
        redisProcess.destroy();
    }
}
