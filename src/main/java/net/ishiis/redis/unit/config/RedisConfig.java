package net.ishiis.redis.unit.config;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RedisConfig {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DEFAULT_REDIS_BINARY_NAME = "redis-server.3.2.5";

    // redis server config format
    private static final String PORT = "--port %d";
    private static final String LOG_FILE = "--logfile %s";
    private static final String TCP_BACKLOG= "--tcp-backlog %d";
    private static final String MAX_CLIENTS = "--maxclients %d";
    private static final String DIR = "--dir %s";
    private static final String PROTECTED_MODE = "--protected-mode %s";
    private static final String DB_FILE_NAME = "--dbfilename %s.rdb";

    // redis master / slave config format
    private static final String SLAVE_OF = "--slaveof 127.0.0.1 %d";

    // redis sentinel config format
    private static final String SENTINEL_MONITOR = "--sentinel monitor %s 127.0.0.1 %d %d";
    private static final String SENTINEL_DOWN_AFTER_MILLISECOND = "--sentinel down-after-milliseconds %s %d";
    private static final String SENTINEL_PARALLEL_SYNCS = "--sentinel parallel-syncs %s %d";
    private static final String SENTINEL_FAILOVER_TIMEOUT = "--sentinel failover-timeout %s %d";

    // cluster config
    private static final String CLUSTER_ENABLED = "--cluster-enabled yes";
    private static final String CLUSTER_CONFIG_FILE = "--cluster-config-file %s";
    private static final String CLUSTER_CONFIG_LINE = "%s 127.0.0.1:%d %smaster - 0 %d %d connected %s";
    private static final String CLUSTER_NODE_TIMEOUT = "--cluster-node-timeout %d";
    private static final String CLUSTER_SLAVE_VALIDITY_FACTOR = "--cluster-slave-validity-factor %d";
    private static final String CLUSTER_MIGRATION_BARRIER = "--cluster-migration-barrier %d";
    private static final String CLUSTER_REQUIRE_FULL_COVERAGE = "--cluster-require-full-coverage %s";

    private Path tempDirectory = Paths.get(System.getProperty("user.dir"), ".redis", String.valueOf(System.currentTimeMillis()));

    // server config
    private Integer port;
    private String redisBinaryPath;
    private Integer maxClients;
    private String dir;
    private Integer tcpBacklog;
    private Integer masterPort;

    // sentinel config
    private String masterName;
    private Integer quorum;
    private Integer downAfterMillisecond;
    private Integer failoverTimeout;
    private Integer parallelSyncs;

    // cluster config
    private Integer nodeTimeout;
    private Integer slaveValidityFactor;
    private Integer migrationBarrier;
    private String requireFullCoverage;


    public RedisConfig(ServerBuilder serverBuilder) {
        this.port = serverBuilder.port;
        this.redisBinaryPath = serverBuilder.redisBinaryPath;
        this.maxClients = serverBuilder.maxClients;
        this.dir = serverBuilder.dir;
        this.masterPort = serverBuilder.masterPort;
        this.tcpBacklog = serverBuilder.tcpBacklog;
    }

    public RedisConfig(SentinelBuilder sentinelBuilder) {
        this.port = sentinelBuilder.port;
        this.redisBinaryPath = sentinelBuilder.redisBinaryPath;
        this.maxClients = sentinelBuilder.maxClients;
        this.dir = sentinelBuilder.dir;
        this.masterName = sentinelBuilder.masterName;
        this.masterPort = sentinelBuilder.masterPort;
        this.tcpBacklog = sentinelBuilder.tcpBacklog;
        this.quorum = sentinelBuilder.quorum;
        this.downAfterMillisecond = sentinelBuilder.downAfterMillisecond;
        this.failoverTimeout = sentinelBuilder.failoverTimeout;
        this.parallelSyncs = sentinelBuilder.parallelSyncs;
    }

    public RedisConfig(ClusterBuilder clusterBuilder) {
        this.port = clusterBuilder.port;
        this.redisBinaryPath = clusterBuilder.redisBinaryPath;
        this.maxClients = clusterBuilder.maxClients;
        this.dir = clusterBuilder.dir;
        this.tcpBacklog = clusterBuilder.tcpBacklog;
        this.nodeTimeout = clusterBuilder.nodeTimeout;
        this.slaveValidityFactor = clusterBuilder.slaveValidityFactor;
        this.migrationBarrier = clusterBuilder.migrationBarrier;
        this.requireFullCoverage = clusterBuilder.requireFullCoverage;
    }

    public static class ServerBuilder {
        private Integer port;
        private String redisBinaryPath;
        private Integer maxClients = 100;
        private String dir = ".";
        private Integer masterPort;
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

        public ServerBuilder dir(String dir) {
            this.dir = dir;
            return this;
        }

        public ServerBuilder tcpBacklog(Integer tcpBacklog) {
            this.tcpBacklog = tcpBacklog;
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
        private Integer maxClients = 100;
        private String dir = ".";
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

        public SentinelBuilder dir(String dir) {
            this.dir = dir;
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

        public RedisConfig build() {
            return new RedisConfig(this);
        }
    }

    public static class ClusterBuilder {
        private Integer port;
        private String redisBinaryPath;
        private Integer maxClients = 100;
        private String dir = ".";
        private Integer tcpBacklog = 16;
        private Integer nodeTimeout = 15000;
        private Integer slaveValidityFactor = 10;
        private Integer migrationBarrier = 1;
        private String requireFullCoverage = "yes";

        public ClusterBuilder(Integer port) {
            this.port = port;
        }

        public ClusterBuilder redisBinaryPath(String redisBinaryPath) {
            this.redisBinaryPath = redisBinaryPath;
            return this;
        }

        public ClusterBuilder maxClients(Integer maxClients) {
            this.maxClients = maxClients;
            return this;
        }

        public ClusterBuilder dir(String dir) {
            this.dir = dir;
            return this;
        }

        public ClusterBuilder tcpBacklog(Integer tcpBacklog) {
            this.tcpBacklog = tcpBacklog;
            return this;
        }

        public ClusterBuilder nodeTimeout(Integer nodeTimeout) {
            this.nodeTimeout = nodeTimeout;
            return this;
        }

        public ClusterBuilder slaveValidityFactor(Integer slaveValidityFactor) {
            this.slaveValidityFactor = slaveValidityFactor;
            return this;
        }

        public ClusterBuilder migrationBarrier(Integer migrationBarrier) {
            this.migrationBarrier = migrationBarrier;
            return this;
        }

        public ClusterBuilder requireFullCoverage(String requireFullCoverage) {
            this.requireFullCoverage = requireFullCoverage;
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

    public String getDir() {
        return dir;
    }

    public Integer getTcpBacklog() {
        return tcpBacklog;
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

    public Integer getNodeTimeout() {
        return nodeTimeout;
    }

    public Integer getSlaveValidityFactor() {
        return slaveValidityFactor;
    }

    public Integer getMigrationBarrier() {
        return migrationBarrier;
    }

    public Path getClusterConfigFile() {
        return Paths.get("nodes-" + port + ".conf");
    }

    public Path getTempDirectory() {
        return tempDirectory;
    }

    public List<String> getCommand() {
        List<String> command = new ArrayList<>();
        command.add(getRedisBinaryPath());
        command.add(getConfigFile().toString());
        command.add(String.format(PORT, getPort()));
        command.add(String.format(LOG_FILE, getLogFile()));
        command.add(String.format(MAX_CLIENTS, getMaxClients()));
        command.add(String.format(DIR, getDir()));
        command.add(String.format(TCP_BACKLOG, getTcpBacklog()));
        command.add(String.format(PROTECTED_MODE, "no"));

        if (masterPort != null && masterName == null) {
            command.add(String.format(SLAVE_OF, getMasterPort()));
        }

        if (masterName != null) {
            command.add("--sentinel");
            command.add(String.format(SENTINEL_MONITOR, getMasterName(), getMasterPort(), getQuorum()));
            command.add(String.format(SENTINEL_DOWN_AFTER_MILLISECOND, getMasterName(), getDownAfterMillisecond()));
            command.add(String.format(SENTINEL_PARALLEL_SYNCS, getMasterName(), getParallelSyncs()));
            command.add(String.format(SENTINEL_FAILOVER_TIMEOUT, getMasterName(), getFailoverTimeout()));
        } else {
            command.add(String.format(DB_FILE_NAME, getPort()));
        }

        if (nodeTimeout != null) {
            command.add(CLUSTER_ENABLED);
            command.add(String.format(CLUSTER_CONFIG_FILE, getClusterConfigFile()));
            command.add(String.format(CLUSTER_NODE_TIMEOUT, getNodeTimeout()));
            command.add(String.format(CLUSTER_SLAVE_VALIDITY_FACTOR, getSlaveValidityFactor()));
            command.add(String.format(CLUSTER_MIGRATION_BARRIER, getMigrationBarrier()));
            command.add(String.format(CLUSTER_REQUIRE_FULL_COVERAGE, "yes"));
        }

        return command;
    }

}
