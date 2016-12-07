package net.ishiis.redis.unit;


import net.ishiis.redis.unit.config.RedisClusterConfig;
import net.ishiis.redis.unit.config.RedisConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RedisCluster implements Redis {
    public static final Integer DEFAULT_REDIS_CLUSTER_PORT = 6379;

    private final List<RedisServer> cluster = new ArrayList<>();

    public RedisCluster() {
        this(DEFAULT_REDIS_CLUSTER_PORT, DEFAULT_REDIS_CLUSTER_PORT +1, DEFAULT_REDIS_CLUSTER_PORT +2);
    }

    public RedisCluster(Integer... clusterPorts) {
        this(Arrays.stream(clusterPorts)
                .map(clusterPort -> new RedisClusterConfig.ClusterBuilder(clusterPort).build())
                .collect(Collectors.toList()));
    }

    public RedisCluster(List<RedisConfig> clusterConfigs) {
        List<Integer> ports = clusterConfigs.stream().map(RedisConfig::getPort).collect(Collectors.toList());
        createClusterConfigFile(ports);
        clusterConfigs.forEach(clusterConfig -> cluster.add(new RedisServer(clusterConfig)));
    }

    @Override
    public void start() {
        cluster.forEach(RedisServer::start);
    }

    @Override
    public void stop() {
        cluster.forEach(RedisServer::stop);
    }

    @Override
    public Boolean isActive() {
        return !cluster.isEmpty() && cluster.stream().allMatch(RedisServer::isActive);
    }

    private void createClusterConfigFile(List<Integer> ports) {
        // TODO: Create cluster config file.
    }

}
