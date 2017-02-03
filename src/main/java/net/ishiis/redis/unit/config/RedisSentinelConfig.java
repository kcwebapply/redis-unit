package net.ishiis.redis.unit.config;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.ishiis.redis.unit.RedisSentinel.WORKING_DIRECTORY;

/**
 * The Redis Sentinel configuration
 */
public class RedisSentinelConfig extends RedisConfig {
    public static final Integer DEFAULT_REDIS_SENTINEL_PORT = 26379;

    // configuration format
    private static final String SENTINEL_MONITOR = "--sentinel monitor %s 127.0.0.1 %d %d";
    private static final String SENTINEL_DOWN_AFTER_MILLISECOND = "--sentinel down-after-milliseconds %s %d";
    private static final String SENTINEL_PARALLEL_SYNCS = "--sentinel parallel-syncs %s %d";
    private static final String SENTINEL_FAILOVER_TIMEOUT = "--sentinel failover-timeout %s %d";

    // sentinel configuration
    private Integer masterPort;
    private String masterName;
    private Integer quorum;
    private Integer downAfterMillisecond;
    private Integer failoverTimeout;
    private Integer parallelSyncs;

    /**
     * Constructor
     * @param sentinelBuilder sentinel builder
     */
    public RedisSentinelConfig(SentinelBuilder sentinelBuilder) {
        this.port = sentinelBuilder.port;
        this.redisBinaryPath = sentinelBuilder.redisBinaryPath;
        this.maxClients = sentinelBuilder.maxClients;
        this.masterName = sentinelBuilder.masterName;
        this.masterPort = sentinelBuilder.masterPort;
        this.tcpBacklog = sentinelBuilder.tcpBacklog;
        this.quorum = sentinelBuilder.quorum;
        this.downAfterMillisecond = sentinelBuilder.downAfterMillisecond;
        this.failoverTimeout = sentinelBuilder.failoverTimeout;
        this.parallelSyncs = sentinelBuilder.parallelSyncs;
    }

    /**
     * Sentinel configuration builder
     */
    public static class SentinelBuilder {
        private Integer port;
        private String redisBinaryPath;
        private Integer maxClients = 100;
        private String masterName = "mymaster";
        private Integer masterPort;
        private Integer tcpBacklog = 16;
        private Integer quorum = 2;
        private Integer downAfterMillisecond = 30000;
        private Integer failoverTimeout = 180000;
        private Integer parallelSyncs = 1;

        public SentinelBuilder(Integer port, Integer masterPort) {
            this.port = port;
            this.masterPort = masterPort;
        }

        public SentinelBuilder redisBinaryPath(String redisBinaryPath) {
            this.redisBinaryPath = redisBinaryPath;
            return this;
        }

        public SentinelBuilder maxClients(Integer maxClients) {
            this.maxClients = maxClients;
            return this;
        }

        public SentinelBuilder masterName(String masterName) {
            this.masterName = masterName;
            return this;
        }

        public SentinelBuilder tcpBacklog(Integer tcpBacklog) {
            this.tcpBacklog = tcpBacklog;
            return this;
        }

        public SentinelBuilder quorum(Integer quorum) {
            this.quorum = quorum;
            return this;
        }

        public SentinelBuilder downAfterMillisecond(Integer downAfterMillisecond) {
            this.downAfterMillisecond = downAfterMillisecond;
            return this;
        }

        public SentinelBuilder failoverTimeout(Integer failoverTimeout) {
            this.failoverTimeout = failoverTimeout;
            return this;
        }

        public SentinelBuilder parallelSyncs(Integer parallelSyncs) {
            this.parallelSyncs = parallelSyncs;
            return this;
        }

        public RedisSentinelConfig build() {
            return new RedisSentinelConfig(this);
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
     * Return master name
     * @return master name
     */
    public String getMasterName() {
        return masterName;
    }

    /**
     * Return the number of quorum
     * @return quorum
     */
    public Integer getQuorum() {
        return quorum;
    }

    /**
     * Return down after millisecond
     * @return down after millisecond
     */
    public Integer getDownAfterMillisecond() {
        return downAfterMillisecond;
    }

    /**
     * Return failover timeout
     * @return failover timeout
     */
    public Integer getFailoverTimeout() {
        return failoverTimeout;
    }

    /**
     * Return parallel syncs
     * @return parallel syncs
     */
    public Integer getParallelSyncs() {
        return parallelSyncs;
    }

    /**
     * Return working directory
     * @return working directory path
     */
    @Override
    public Path getWorkingDirectory() {
        return WORKING_DIRECTORY;
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
        command.addAll(Arrays.asList(String.format(TCP_BACKLOG, getTcpBacklog()).split(" ")));
        command.addAll(Arrays.asList(String.format(PROTECTED_MODE, "no").split(" ")));

        command.add("--sentinel");
        command.addAll(Arrays.asList(String.format(SENTINEL_MONITOR, getMasterName(), getMasterPort(), getQuorum()).split(" ")));
        command.addAll(Arrays.asList(String.format(SENTINEL_DOWN_AFTER_MILLISECOND, getMasterName(), getDownAfterMillisecond()).split(" ")));
        command.addAll(Arrays.asList(String.format(SENTINEL_PARALLEL_SYNCS, getMasterName(), getParallelSyncs()).split(" ")));
        command.addAll(Arrays.asList(String.format(SENTINEL_FAILOVER_TIMEOUT, getMasterName(), getFailoverTimeout()).split(" ")));

        return command;
    }

}
