package net.ishiis.redis.unit.config;


import java.util.ArrayList;
import java.util.List;

public class RedisSentinelConfig extends RedisConfig {
    public static final Integer DEFAULT_REDIS_SENTINEL_PORT = 26379;

    // config format
    private static final String SENTINEL_MONITOR = "--sentinel monitor %s 127.0.0.1 %d %d";
    private static final String SENTINEL_DOWN_AFTER_MILLISECOND = "--sentinel down-after-milliseconds %s %d";
    private static final String SENTINEL_PARALLEL_SYNCS = "--sentinel parallel-syncs %s %d";
    private static final String SENTINEL_FAILOVER_TIMEOUT = "--sentinel failover-timeout %s %d";

    // sentinel config
    private Integer masterPort;
    private String masterName;
    private Integer quorum;
    private Integer downAfterMillisecond;
    private Integer failoverTimeout;
    private Integer parallelSyncs;

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

    public Integer getMasterPort() {
        return masterPort;
    }

    public String getMasterName() {
        return masterName;
    }

    public Integer getQuorum() {
        return quorum;
    }

    public Integer getDownAfterMillisecond() {
        return downAfterMillisecond;
    }

    public Integer getFailoverTimeout() {
        return failoverTimeout;
    }

    public Integer getParallelSyncs() {
        return parallelSyncs;
    }

    public List<String> getCommand() {
        List<String> command = new ArrayList<>();
        command.add(getRedisBinaryPath());
        command.add(getConfigFile().toString());
        command.add(String.format(PORT, getPort()));
        command.add(String.format(LOG_FILE, getLogFile()));
        command.add(String.format(MAX_CLIENTS, getMaxClients()));
        command.add(DIR);
        command.add(String.format(TCP_BACKLOG, getTcpBacklog()));
        command.add(String.format(PROTECTED_MODE, "no"));
        command.add("--sentinel");
        command.add(String.format(SENTINEL_MONITOR, getMasterName(), getMasterPort(), getQuorum()));
        command.add(String.format(SENTINEL_DOWN_AFTER_MILLISECOND, getMasterName(), getDownAfterMillisecond()));
        command.add(String.format(SENTINEL_PARALLEL_SYNCS, getMasterName(), getParallelSyncs()));
        command.add(String.format(SENTINEL_FAILOVER_TIMEOUT, getMasterName(), getFailoverTimeout()));

        return command;
    }

}
