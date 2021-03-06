package net.ishiis.redis.unit.config;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * The Redis configuration
 *
 * @see RedisServerConfig
 * @see RedisMasterSlaveConfig
 * @see RedisSentinelConfig
 * @see RedisClusterConfig
 */
public abstract class RedisConfig {
    private static final Path REDIS_DIRECTORY = Paths.get(System.getProperty("user.dir"), ".redis");
    private static final String REDIS_BINARY_LINUX_64 = "redis-server.3.2.5";
    private static final String REDIS_BINARY_LINUX_32 = "redis-server.3.2.5_32";
    private static final String REDIS_BINARY_OSX = "redis-server.3.2.5.app";
    private static final String REDIS_BINARY_WINDOWS_64 = "redis-server.3.2.100.exe";

    private static final String OPERATING_SYSTEM_NOT_SUPPORT = "OS: %s, ARCH: %s";

    // configuration format
    protected static final String PORT = "--port %d";
    protected static final String LOG_FILE = "--logfile %s";
    protected static final String TCP_BACKLOG= "--tcp-backlog %d";
    protected static final String MAX_CLIENTS = "--maxclients %d";
    protected static final String DIR = "--dir .";
    protected static final String PROTECTED_MODE = "--protected-mode %s";

    // redis configuration
    protected Integer port;
    protected String redisBinaryPath;
    protected Integer maxClients;
    protected Integer tcpBacklog;

    public Integer getPort() {
        return port;
    }

    /**
     * Return redis binary path
     *
     * @throws {@link RuntimeException} if unsupported environment
     * @return binary path
     */
    public String getRedisBinaryPath() {
        if (redisBinaryPath == null) {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                String arch = System.getProperty("os.arch");
                String binaryName;
                if (os.contains("win")) {
                    if (arch.contains("64")) {
                        binaryName = REDIS_BINARY_WINDOWS_64;
                    } else {
                        throw new RuntimeException("Operating system is not supported. "
                                + String.format(OPERATING_SYSTEM_NOT_SUPPORT, os, arch));
                    }
                } else if (os.contains("linux")) {
                    if (arch.contains("64")) {
                        binaryName = REDIS_BINARY_LINUX_64;
                    } else {
                        binaryName = REDIS_BINARY_LINUX_32;
                    }
                } else if (os.contains("mac")) {
                    binaryName = REDIS_BINARY_OSX;
                } else {
                    throw new RuntimeException("Operating system is not supported. "
                            + String.format(OPERATING_SYSTEM_NOT_SUPPORT, os, arch));
                }

                String path = new File(URLDecoder.decode(
                        this.getClass().getClassLoader().getResource(binaryName).getFile(), "UTF-8")).getPath();

                // unit test safe
                if (!path.contains(".jar!")) {
                    return path;
                }

                // copy from in jar
                URL redisUrl = this.getClass().getClassLoader().getResource(binaryName);
                if (!REDIS_DIRECTORY.toFile().exists()) REDIS_DIRECTORY.toFile().mkdir();
                if (!Paths.get(REDIS_DIRECTORY.toString(), binaryName).toFile().exists()) {
                    FileUtils.copyURLToFile(redisUrl, Paths.get(REDIS_DIRECTORY.toString(), binaryName).toFile());
                }
                return Paths.get(REDIS_DIRECTORY.toString(), binaryName).toString();

            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 is not supported encoding.", e);
            } catch (IOException e) {
                throw new RuntimeException("Unable to create resource.", e);
            }
        }
        return redisBinaryPath;
    }

    /**
     * Return configuration file path
     *
     * @return configuration file path
     */
    public Path getConfigFile() {
        return Paths.get(port + ".conf");
    }

    /**
     * Return log file path
     *
     * @return log file path
     */
    public Path getLogFile() {
        return Paths.get(port + ".log");
    }

    /**
     * Return the number of max client
     *
     * @return max client
     */
    public Integer getMaxClients() {
        return maxClients;
    }

    /**
     * Return tcp back log
     * @return tcp back log
     */
    public Integer getTcpBacklog() {
        return tcpBacklog;
    }

    public abstract Path getWorkingDirectory();

    public abstract List<String> getCommand();

}
