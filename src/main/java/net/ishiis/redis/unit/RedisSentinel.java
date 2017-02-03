package net.ishiis.redis.unit;


import net.ishiis.redis.unit.config.RedisMasterSlaveConfig;
import net.ishiis.redis.unit.config.RedisSentinelConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.ishiis.redis.unit.config.RedisSentinelConfig.DEFAULT_REDIS_SENTINEL_PORT;
import static net.ishiis.redis.unit.config.RedisServerConfig.DEFAULT_REDIS_SERVER_PORT;

/**
 * Redis sentinel
 */
public class RedisSentinel implements Redis {
    public static final Path WORKING_DIRECTORY = Paths.get(System.getProperty("user.dir"), ".redis", String.valueOf(System.currentTimeMillis()));

    private final RedisMasterSlave masterSlave;
    private final List<RedisServer> sentinels = new ArrayList<>();

    /**
     * Default constructor
     */
    public RedisSentinel() {
        this(DEFAULT_REDIS_SENTINEL_PORT, DEFAULT_REDIS_SENTINEL_PORT + 1, DEFAULT_REDIS_SENTINEL_PORT +2);
    }

    /**
     * Constructor
     * @param sentinelPorts array of sentinel port
     */
    public RedisSentinel(Integer... sentinelPorts) {
        this(new RedisMasterSlaveConfig.MasterBuilder(DEFAULT_REDIS_SERVER_PORT).build(),
                Collections.singletonList(new RedisMasterSlaveConfig
                        .SlaveBuilder(DEFAULT_REDIS_SERVER_PORT + 1, DEFAULT_REDIS_SERVER_PORT).build()),
                Arrays.stream(sentinelPorts)
                        .map(sentinelPort ->
                                new RedisSentinelConfig.SentinelBuilder(sentinelPort, DEFAULT_REDIS_SERVER_PORT).build())
                        .collect(Collectors.toList()));
    }

    /**
     * Constructor
     * @param masterConfig master configuration
     * @param slaveConfigs slave configuration list
     * @param sentinelConfigs sentinel configuration list
     */
    public RedisSentinel(RedisMasterSlaveConfig masterConfig, List<RedisMasterSlaveConfig> slaveConfigs,
                         List<RedisSentinelConfig> sentinelConfigs) {
        masterSlave = new RedisMasterSlave(masterConfig, slaveConfigs);
        sentinelConfigs.forEach(sentinelConfig -> sentinels.add(new RedisServer(sentinelConfig)));
    }

    /**
     * Start master/slave node and sentinel nodes
     */
    @Override
    public void start() {
        masterSlave.start();
        sentinels.forEach(RedisServer::start);
    }

    /**
     * Stop master/slave node and sentinel nodes
     */
    @Override
    public void stop() {
        masterSlave.stop();
        sentinels.forEach(RedisServer::stop);
    }

    /**
     * Return whether all node is alive
     * @return {@code true} if all node is active, {@code false} otherwise
     */
    @Override
    public Boolean isActive() {
        return masterSlave != null && !sentinels.isEmpty() && masterSlave.isActive()
                && sentinels.stream().allMatch(RedisServer::isActive);
    }
}
