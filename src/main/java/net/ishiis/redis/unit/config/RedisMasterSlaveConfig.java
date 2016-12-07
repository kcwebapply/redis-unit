package net.ishiis.redis.unit.config;


import java.util.ArrayList;
import java.util.List;

public class RedisMasterSlaveConfig extends RedisConfig {

    // config format
    private static final String SLAVE_OF = "--slaveof 127.0.0.1 %d";
    private static final String DB_FILE_NAME = "--dbfilename %s.rdb";

    private Integer masterPort;

    public RedisMasterSlaveConfig(MasterBuilder masterBuilder) {
        this.port = masterBuilder.port;
        this.redisBinaryPath = masterBuilder.redisBinaryPath;
        this.maxClients = masterBuilder.maxClients;
        this.tcpBacklog = masterBuilder.tcpBacklog;
    }

    public RedisMasterSlaveConfig(SlaveBuilder slaveBuilder) {
        this.port = slaveBuilder.port;
        this.redisBinaryPath = slaveBuilder.redisBinaryPath;
        this.maxClients = slaveBuilder.maxClients;
        this.masterPort = slaveBuilder.masterPort;
        this.tcpBacklog = slaveBuilder.tcpBacklog;
    }

    public static class MasterBuilder {
        private Integer port;
        private String redisBinaryPath;
        private Integer maxClients = 100;
        private Integer tcpBacklog = 16;

        public MasterBuilder(Integer port) {
            this.port = port;
        }

        public MasterBuilder redisBinaryPath(String redisBinaryPath) {
            this.redisBinaryPath = redisBinaryPath;
            return this;
        }

        public MasterBuilder maxClients(Integer maxClients) {
            this.maxClients = maxClients;
            return this;
        }

        public MasterBuilder tcpBacklog(Integer tcpBacklog) {
            this.tcpBacklog = tcpBacklog;
            return this;
        }

        public RedisMasterSlaveConfig build() {
            return new RedisMasterSlaveConfig(this);
        }
    }

    public static class SlaveBuilder {
        private Integer port;
        private String redisBinaryPath;
        private Integer maxClients = 100;
        private Integer masterPort;
        private Integer tcpBacklog = 16;

        public SlaveBuilder(Integer port, Integer masterPort) {
            this.port = port;
            this.masterPort = masterPort;
        }

        public SlaveBuilder redisBinaryPath(String redisBinaryPath) {
            this.redisBinaryPath = redisBinaryPath;
            return this;
        }

        public SlaveBuilder maxClients(Integer maxClients) {
            this.maxClients = maxClients;
            return this;
        }

        public SlaveBuilder tcpBacklog(Integer tcpBacklog) {
            this.tcpBacklog = tcpBacklog;
            return this;
        }

        public RedisMasterSlaveConfig build() {
            return new RedisMasterSlaveConfig(this);
        }
    }

    public Integer getMasterPort() {
        return masterPort;
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

        if (masterPort != null) {
            command.add(String.format(SLAVE_OF, getMasterPort()));
        }

        return command;
    }


}
