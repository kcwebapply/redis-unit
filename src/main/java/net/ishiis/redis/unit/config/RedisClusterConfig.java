package net.ishiis.redis.unit.config;


import net.ishiis.redis.unit.RedisCluster;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RedisClusterConfig extends RedisConfig {
    public static final int DEFAULT_REDIS_CLUSTER_PORT = 6379;

    // config format
    private static final String DB_FILE_NAME = "--dbfilename %s.rdb";
    private static final String CLUSTER_ENABLED = "--cluster-enabled yes";
    private static final String CLUSTER_CONFIG_FILE = "--cluster-config-file %s";
    private static final String CLUSTER_NODE_TIMEOUT = "--cluster-node-timeout %d";
    private static final String CLUSTER_SLAVE_VALIDITY_FACTOR = "--cluster-slave-validity-factor %d";
    private static final String CLUSTER_MIGRATION_BARRIER = "--cluster-migration-barrier %d";
    private static final String CLUSTER_REQUIRE_FULL_COVERAGE = "--cluster-require-full-coverage %s";

    // cluster config
    private Integer nodeTimeout;
    private Integer slaveValidityFactor;
    private Integer migrationBarrier;
    private String requireFullCoverage;

    public RedisClusterConfig(ClusterBuilder clusterBuilder) {
        this.port = clusterBuilder.port;
        this.redisBinaryPath = clusterBuilder.redisBinaryPath;
        this.maxClients = clusterBuilder.maxClients;
        this.tcpBacklog = clusterBuilder.tcpBacklog;
        this.nodeTimeout = clusterBuilder.nodeTimeout;
        this.slaveValidityFactor = clusterBuilder.slaveValidityFactor;
        this.migrationBarrier = clusterBuilder.migrationBarrier;
        this.requireFullCoverage = clusterBuilder.requireFullCoverage;
    }

    public static class ClusterBuilder {
        private Integer port;
        private String redisBinaryPath;
        private Integer maxClients = 100;
        private Integer tcpBacklog = 16;
        private Integer nodeTimeout = 15000;
        private Integer slaveValidityFactor = 10;
        private Integer migrationBarrier = 1;
        private String requireFullCoverage = "yes";

        public ClusterBuilder(Integer port, Integer... otherPorts) {
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

        public RedisClusterConfig build() {
            return new RedisClusterConfig(this);
        }
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

    @Override
    public Path getWorkingDirectory() {
        return RedisCluster.WORKING_DIRECTORY;
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
        command.add(CLUSTER_ENABLED);
        command.add(String.format(CLUSTER_CONFIG_FILE, getClusterConfigFile()));
        command.add(String.format(CLUSTER_NODE_TIMEOUT, getNodeTimeout()));
        command.add(String.format(CLUSTER_SLAVE_VALIDITY_FACTOR, getSlaveValidityFactor()));
        command.add(String.format(CLUSTER_MIGRATION_BARRIER, getMigrationBarrier()));
        command.add(String.format(CLUSTER_REQUIRE_FULL_COVERAGE, "yes"));

        return command;
    }

}
