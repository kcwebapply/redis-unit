package net.ishiis.redis.unit.config;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public abstract class RedisConfig {
    private static final String REDIS_BINARY_LINUX_64 = "redis-server.3.2.5";
    private static final String REDIS_BINARY_WINDOWS_64 = "redis-server.3.2.100.exe";

    private static final String OPERATING_SYSTEM_NOT_SUPPORT = "OS: %s, ARCH: %s";

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
                String os = System.getProperty("os.name").toLowerCase();
                String arch = System.getProperty("os.arch");
                String binaryPath;
                if (os.contains("win")) {
                    if (arch.contains("64")) {
                        binaryPath = REDIS_BINARY_WINDOWS_64;
                    } else {
                        throw new RuntimeException("Operating is not supported. "
                                + String.format(OPERATING_SYSTEM_NOT_SUPPORT, os, arch));
                    }
                } else if (os.contains("linux")) {
                    if (arch.contains("64")) {
                        binaryPath = REDIS_BINARY_LINUX_64;
                    } else {
                        // TODO: add Linux 32bit binary
                        binaryPath = null;
                    }
                } else if (os.contains("mac")) {
                    //TODO: add mac binary
                    binaryPath = null;
                } else {
                    throw new RuntimeException("Operating is not supported. "
                            + String.format(OPERATING_SYSTEM_NOT_SUPPORT, os, arch));
                }
                return new File(URLDecoder.decode(
                        this.getClass().getClassLoader().getResource(binaryPath).getFile(), "UTF-8")).getPath();
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
