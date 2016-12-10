package net.ishiis.redis.unit.config;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RedisServerConfig extends RedisConfig {
    public static final Integer DEFAULT_REDIS_SERVER_PORT = 6379;
    public static final Path WORKING_DIRECTORY = Paths.get(System.getProperty("user.dir"), ".redis", String.valueOf(System.currentTimeMillis()));

    // config format
    private static final String DB_FILE_NAME = "--dbfilename %s.rdb";

    public RedisServerConfig(ServerBuilder serverBuilder) {
        this.port = serverBuilder.port;
        this.redisBinaryPath = serverBuilder.redisBinaryPath;
        this.maxClients = serverBuilder.maxClients;
        this.tcpBacklog = serverBuilder.tcpBacklog;
    }

    public static class ServerBuilder {
        private Integer port;
        private String redisBinaryPath;
        private Integer maxClients = 100;
        private Integer tcpBacklog = 16;

        public ServerBuilder(Integer port) {
            this.port = port;
        }

        public ServerBuilder redisBinaryPath(String redisBinaryPath) {
            this.redisBinaryPath = redisBinaryPath;
            return this;
        }

        public ServerBuilder maxClients(Integer maxClients) {
            this.maxClients = maxClients;
            return this;
        }

        public ServerBuilder tcpBacklog(Integer tcpBacklog) {
            this.tcpBacklog = tcpBacklog;
            return this;
        }

        public RedisServerConfig build() {
            return new RedisServerConfig(this);
        }
    }

    public Path getWorkingDirectory() {
        return WORKING_DIRECTORY;
    }

    public List<String> getCommand() {
        List<String> command = new ArrayList<>();
        command.add(getRedisBinaryPath());
        command.add(getConfigFile().toString());
        command.add(String.format(PORT, getPort()));
        command.add(String.format(LOG_FILE, getLogFile()));
        command.add(String.format(MAX_CLIENTS, getMaxClients()));
        command.add(DIR);
        command.add(String.format(DB_FILE_NAME, getPort()));
        command.add(String.format(TCP_BACKLOG, getTcpBacklog()));
        command.add(String.format(PROTECTED_MODE, "no"));

        return command;
    }

}