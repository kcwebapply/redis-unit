package net.ishiis.redis.unit.config;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public abstract class RedisConfig {
    private static final String DEFAULT_REDIS_BINARY_NAME = "redis-server.3.2.5";

    // config format
    protected static final String PORT = "--port %d";
    protected static final String LOG_FILE = "--logfile %s";
    protected static final String TCP_BACKLOG= "--tcp-backlog %d";
    protected static final String MAX_CLIENTS = "--maxclients %d";
    protected static final String DIR = "--dir .";
    protected static final String PROTECTED_MODE = "--protected-mode %s";

    // redis config
    protected Integer port;
    protected String redisBinaryPath;
    protected Integer maxClients;
    protected Integer tcpBacklog;

    public Integer getPort() {
        return port;
    }

    public String getRedisBinaryPath() {
        if (redisBinaryPath == null) {
            try {
                return URLDecoder.decode(
                        this.getClass().getClassLoader().getResource(DEFAULT_REDIS_BINARY_NAME).getPath(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 is not supported encoding.", e);
            }
        }
        return redisBinaryPath;
    }

    public Path getConfigFile() {
        return Paths.get(port + ".conf");
    }

    public Path getLogFile() {
        return Paths.get(port + ".log");
    }

    public Integer getMaxClients() {
        return maxClients;
    }

    public Integer getTcpBacklog() {
        return tcpBacklog;
    }

    public abstract Path getWorkingDirectory();

    public abstract List<String> getCommand();

}
