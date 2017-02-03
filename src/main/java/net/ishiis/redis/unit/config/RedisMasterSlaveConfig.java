package net.ishiis.redis.unit.config;


import net.ishiis.redis.unit.RedisMasterSlave;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Redis master/slave configuration
 */
public class RedisMasterSlaveConfig extends RedisConfig {

    // config format
    private static final String SLAVE_OF = "--slaveof 127.0.0.1 %d";
    private static final String DB_FILE_NAME = "--dbfilename %s.rdb";

    private Integer masterPort;

    /**
     * Constructor
     * @param masterBuilder master builder
     */
    public RedisMasterSlaveConfig(MasterBuilder masterBuilder) {
        this.port = masterBuilder.port;
        this.redisBinaryPath = masterBuilder.redisBinaryPath;
        this.maxClients = masterBuilder.maxClients;
        this.tcpBacklog = masterBuilder.tcpBacklog;
    }

    /**
     * Constructor
     * @param slaveBuilder slave builder
     */
    public RedisMasterSlaveConfig(SlaveBuilder slaveBuilder) {
        this.port = slaveBuilder.port;
        this.redisBinaryPath = slaveBuilder.redisBinaryPath;
        this.maxClients = slaveBuilder.maxClients;
        this.masterPort = slaveBuilder.masterPort;
        this.tcpBacklog = slaveBuilder.tcpBacklog;
    }

    /**
     * Master configuration builder
     */
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

    /**
     * Slave configuration builder
     */
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

    /**
     * Return master port
     * @return master port
     */
    public Integer getMasterPort() {
        return masterPort;
    }

    /**
     * Return working directory
     * @return working directory path
     */
    @Override
    public Path getWorkingDirectory() {
        return RedisMasterSlave.WORKING_DIRECTORY;
    }

    /**
     * Return command list
     *
     * @return command list
     */
    @Override
    public List<String> getCommand() {
        List<String> command = new ArrayList<>();
        command.add(getRedisBinaryPath());
        command.add(getConfigFile().toString());
        command.addAll(Arrays.asList(String.format(PORT, getPort()).split(" ")));
        command.addAll(Arrays.asList(String.format(LOG_FILE, getLogFile()).split(" ")));
        command.addAll(Arrays.asList(String.format(MAX_CLIENTS, getMaxClients()).split(" ")));
        command.addAll(Arrays.asList(DIR.split(" ")));
        command.addAll(Arrays.asList(String.format(DB_FILE_NAME, getPort()).split(" ")));
        command.addAll(Arrays.asList(String.format(TCP_BACKLOG, getTcpBacklog()).split(" ")));
        command.addAll(Arrays.asList(String.format(PROTECTED_MODE, "no").split(" ")));

        if (masterPort != null) {
            command.addAll(Arrays.asList(String.format(SLAVE_OF, getMasterPort()).split(" ")));
        }

        return command;
    }


}
