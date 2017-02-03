package net.ishiis.redis.unit;


import net.ishiis.redis.unit.config.RedisClusterConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.ishiis.redis.unit.config.RedisClusterConfig.DEFAULT_REDIS_CLUSTER_PORT;

/**
 * Redis cluster
 */
public class RedisCluster implements Redis {
    public static final Path WORKING_DIRECTORY = Paths.get(System.getProperty("user.dir"), ".redis", String.valueOf(System.currentTimeMillis()));
    public static final int REDIS_CLUSTER_SLOTS = 16383;

    private static final String CLUSTER_CONFIG_LINE = "%s 127.0.0.1:%d %smaster - 0 %d %d connected %s";
    private static final String CLUSTER_CONFIG_END_LINE = "vars currentEpoch %d lastVoteEpoch 0";
    private final List<RedisServer> cluster = new ArrayList<>();

    /**
     * Default constructor
     */
    public RedisCluster() {
        this(DEFAULT_REDIS_CLUSTER_PORT, DEFAULT_REDIS_CLUSTER_PORT +1, DEFAULT_REDIS_CLUSTER_PORT +2);
    }

    /**
     * Constructor
     * @param clusterPorts array of cluster port
     */
    public RedisCluster(Integer... clusterPorts) {
        this(Arrays.stream(clusterPorts)
                .map(clusterPort -> new RedisClusterConfig.ClusterBuilder(clusterPort).build())
                .collect(Collectors.toList()));
    }

    /**
     * Constructor
     * @param clusterConfigs {@link RedisClusterConfig} list
     */
    public RedisCluster(List<RedisClusterConfig> clusterConfigs) {
        createClusterConfigFile(clusterConfigs);
        clusterConfigs.forEach(clusterConfig -> cluster.add(new RedisServer(clusterConfig)));
    }

    /**
     * Start all redis cluster node
     */
    @Override
    public void start() {
        cluster.forEach(RedisServer::start);
    }

    /**
     * Stop all redis cluster node
     */
    @Override
    public void stop() {
        cluster.forEach(RedisServer::stop);
    }

    /**
     * Return whether cluster is alive
     * @return {@code true} if all redis cluster node is active, {@code false} otherwise
     */
    @Override
    public Boolean isActive() {
        return !cluster.isEmpty() && cluster.stream().allMatch(RedisServer::isActive);
    }

    /**
     * Create cluster configuration file
     * @param clusterConfigs {@link RedisClusterConfig} list
     * @throws {@link RuntimeException} when file can not be created
     */
    private void createClusterConfigFile(List<RedisClusterConfig> clusterConfigs) {
        int size = clusterConfigs.size();
        List<String> slots = getSlotRangeList(size);
        for (int i = 0; i < size; i++){
            List<String> configLine = new ArrayList<>();
            configLine.add(String.format(CLUSTER_CONFIG_LINE, toEncryptedHash(clusterConfigs.get(i).getPort().toString()),
                    clusterConfigs.get(i).getPort(), "myself,", 0, i, slots.get(i)));
            for (int j = 0; j < size; j++) {
                if(i != j) {
                    configLine.add(String.format(CLUSTER_CONFIG_LINE, toEncryptedHash(clusterConfigs.get(j).getPort().toString()),
                            clusterConfigs.get(j).getPort(), "", System.currentTimeMillis(), j, slots.get(j)));
                }
            }
            configLine.add(String.format(CLUSTER_CONFIG_END_LINE, size - 1));
            Path configPath = Paths.get(WORKING_DIRECTORY.toString(), clusterConfigs.get(i).getClusterConfigFile().toString());
            try {
                Files.createDirectories(WORKING_DIRECTORY);
                Files.write(configPath, configLine, StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException("Unable to create a cluster config.");
            }
        }
    }

    private List<String> getSlotRangeList(int nodes) {
        double slot = REDIS_CLUSTER_SLOTS / nodes;
        return IntStream.range(0, nodes).boxed().map(x -> {
            if (x == 0) {
                return "0-" + (int) Math.floor(slot * (x + 1));
            } else if (x == nodes -1) {
                return (int) Math.floor(slot * x + 1) + "-" + REDIS_CLUSTER_SLOTS;
            } else {
                return (int) Math.floor(slot * x + 1)  + "-" + (int) Math.floor(slot * (x + 1));
            }
        }).collect(Collectors.toList());
    }

    private  String toEncryptedHash(String str) {
        StringBuilder result = new StringBuilder();
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA");
            for (byte b : messageDigest.digest(str.getBytes())) {
                result.append(String.format("%02x", b));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
