package net.ishiis.redis.unit;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class RedisServer implements Redis {
    public static final Integer DEFAULT_REDIS_SERVER_PORT = 6379;

    private static final String REDIS_BINARY_NAME = "redis-server.3.2.5";

    private List<String> command;

    private Process redisProcess;

    public RedisServer() {
        this(DEFAULT_REDIS_SERVER_PORT);
    }

    public RedisServer(Integer port) {
        // Set a default Redis binary path.
        String redisBinaryPath;
        try {
            redisBinaryPath = URLDecoder.decode(
                    this.getClass().getClassLoader().getResource(REDIS_BINARY_NAME).getPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is not supported Encoding.", e);
        }

        this.command = Arrays.asList(redisBinaryPath, "--port", Integer.toString(port));
    }

    public RedisServer(String redisBinaryPath) {
        this(redisBinaryPath, DEFAULT_REDIS_SERVER_PORT);
    }

    public RedisServer(String redisBinaryPath, Integer port) {
        this.command = Arrays.asList(redisBinaryPath, "--port", Integer.toString(port));
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
            redisProcess = pb.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start Redis.", e);
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(redisProcess.getInputStream()))) {
            String outputLine;
            do {
                outputLine = bufferedReader.readLine();
                if (outputLine == null) {
                    throw new RuntimeException("Output line does not exist.");
                }
                if (outputLine.contains("Address already in use")) {
                    throw new RuntimeException("Address already in use.");
                }
            } while (!outputLine.contains("The server is now ready to accept connections on port"));
        } catch (IOException e) {
            throw new RuntimeException("IOException: ", e);
        }
    }

    @Override
    public void stop() {
        redisProcess.destroy();
        if (redisProcess.isAlive()){
            try {
                redisProcess.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException("Can not stop Redis process.");
            }
        }
    }

    @Override
    public Boolean isAlive() {
        return redisProcess != null && redisProcess.isAlive();
    }

}
