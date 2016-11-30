package net.ishiis.redis.unit;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RedisConfig {
    private static final String DEFAULT_REDIS_BINARY_NAME = "redis-server.3.2.5";

    // redis server config format
    private static final String PORT = "--port %d ";
    private static final String LOG_FILE = "--logfile %s ";
    private static final String MAX_CLIENTS = "--maxclients %d ";

    // redis master / slave config format
    private static final String SLAVE_OF = "--slaveof 127.0.0.1 %d ";

    // redis sentinel config format
    private static final String SENTINEL_MONITOR = "--sentinel monitor %s 127.0.0.1 %d %d";
    private static final String SENTINEL_DOWN_AFTER_MILLISECOND = "--sentinel down-after-milliseconds %s %d";
    private static final String SENTINEL_PARALLEL_SYNCS = "--sentinel parallel-syncs %s %d";
    private static final String SENTINEL_FAILOVER_TIMEOUT = "--sentinel failover-timeout %s %d";

    // server config
    private Integer port;
    private String redisBinaryPath;
    private Integer maxClients;
    private Integer masterPort;

    // sentinel config
    private String masterName;
    private Integer quorum;
    private Integer downAfterMillisecond;
    private Integer failoverTimeout;
    private Integer parallelSyncs;

    public RedisConfig(ServerBuilder serverBuilder) {
        this.port = serverBuilder.port;
        this.redisBinaryPath = serverBuilder.redisBinaryPath;
        this.maxClients = serverBuilder.maxClients;
        this.masterPort = serverBuilder.masterPort;
    }

    public RedisConfig(SentinelBuilder sentinelBuilder) {
        this.port = sentinelBuilder.port;
        this.redisBinaryPath = sentinelBuilder.redisBinaryPath;
        this.masterName = sentinelBuilder.masterName;
        this.masterPort = sentinelBuilder.masterPort;
        this.quorum = sentinelBuilder.quorum;
        this.downAfterMillisecond = sentinelBuilder.downAfterMillisecond;
        this.failoverTimeout = sentinelBuilder.failoverTimeout;
        this.parallelSyncs = sentinelBuilder.parallelSyncs;
    }

    public static class ServerBuilder {
        private Integer port;
        private String redisBinaryPath;
        private Integer maxClients = 100;  // default
        private Integer masterPort;

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

        public ServerBuilder masterPort(Integer masterPort) {
            this.masterPort = masterPort;
            return this;
        }

        public RedisConfig build() {
            return new RedisConfig(this);
        }
    }

    public static class SentinelBuilder {
        private Integer port;
        private String redisBinaryPath;
        private String masterName;
        private Integer masterPort;
        private Integer quorum = 2;
        private Integer downAfterMillisecond = 30000;
        private Integer failoverTimeout = 180000;
        private Integer parallelSyncs = 1;

        public SentinelBuilder(Integer port) {
            this.port = port;
        }

        public SentinelBuilder redisBinaryPath(String redisBinaryPath) {
            this.redisBinaryPath = redisBinaryPath;
            return this;
        }

        public SentinelBuilder masterName(String masterName) {
            this.masterName = masterName;
            return this;
        }

        public SentinelBuilder masterPort(Integer masterPort) {
            this.masterPort = masterPort;
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

        public RedisConfig build() {
            return new RedisConfig(this);
        }
    }

    public Integer getPort() {
        return port;
    }

    public String getRedisBinaryPath() {
        if (redisBinaryPath == null) {
            try {
                return URLDecoder.decode(
                        this.getClass().getClassLoader().getResource(DEFAULT_REDIS_BINARY_NAME).getPath(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 is not supported Encoding.", e);
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
        if (masterPort != null) {
            command.add(String.format(SLAVE_OF, getMasterPort()));
        }

        return command;
    }

}
