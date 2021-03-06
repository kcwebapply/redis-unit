package net.ishiis.redis.unit.config;


import net.ishiis.redis.unit.RedisCluster;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Redis Cluster configuration
 */
public class RedisClusterConfig extends RedisConfig {
    public static final int DEFAULT_REDIS_CLUSTER_PORT = 6379;

    // configuration format
    private static final String DB_FILE_NAME = "--dbfilename %s.rdb";
    private static final String CLUSTER_ENABLED = "--cluster-enabled yes";
    private static final String CLUSTER_CONFIG_FILE = "--cluster-config-file %s";
    private static final String CLUSTER_NODE_TIMEOUT = "--cluster-node-timeout %d";
    private static final String CLUSTER_SLAVE_VALIDITY_FACTOR = "--cluster-slave-validity-factor %d";
    private static final String CLUSTER_MIGRATION_BARRIER = "--cluster-migration-barrier %d";
    private static final String CLUSTER_REQUIRE_FULL_COVERAGE = "--cluster-require-full-coverage %s";

    // cluster configuration
    private Integer nodeTimeout;
    private Integer slaveValidityFactor;
    private Integer migrationBarrier;
    private String requireFullCoverage;

    /**
     * Constructor
     * @param clusterBuilder cluster builder
     */
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

    /**
     * Cluster builder
     */
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

    /**
     * Return node timeout
     * @return node timeout
     */
    public Integer getNodeTimeout() {
        return nodeTimeout;
    }

    /**
     * Return slave validity factor
     * @return slave validity factor
     */
    public Integer getSlaveValidityFactor() {
        return slaveValidityFactor;
    }

    /**
     * Return migration barrier
     * @return migration barrier
     */
    public Integer getMigrationBarrier() {
        return migrationBarrier;
    }

    /**
     * Return cluster configuration file path
     * @return cluster configuration file path
     */
    public Path getClusterConfigFile() {
        return Paths.get("nodes-" + port + ".conf");
    }

    /**
     * Return working directory
     * @return working directory path
     */
    @Override
    public Path getWorkingDirectory() {
        return RedisCluster.WORKING_DIRECTORY;
    }

    /**
     * Return command list
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

        command.addAll(Arrays.asList(CLUSTER_ENABLED.split(" ")));
        command.addAll(Arrays.asList(String.format(CLUSTER_CONFIG_FILE, getClusterConfigFile()).split(" ")));
        command.addAll(Arrays.asList(String.format(CLUSTER_NODE_TIMEOUT, getNodeTimeout()).split(" ")));
        command.addAll(Arrays.asList(String.format(CLUSTER_SLAVE_VALIDITY_FACTOR, getSlaveValidityFactor()).split(" ")));
        command.addAll(Arrays.asList(String.format(CLUSTER_MIGRATION_BARRIER, getMigrationBarrier()).split(" ")));
        command.addAll(Arrays.asList(String.format(CLUSTER_REQUIRE_FULL_COVERAGE, "yes").split(" ")));

        return command;
    }

}
