package net.ishiis.redis.unit;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.ishiis.redis.unit.RedisServer.DEFAULT_REDIS_SERVER_PORT;

public class RedisSentinel implements Redis {
    public static final Integer DEFAULT_REDIS_SENTINEL_PORT = 26379;

    private final RedisMasterSlave masterSlave;
    private final List<RedisServer> sentinels = new ArrayList<>();

    public RedisSentinel() {
        this(DEFAULT_REDIS_SERVER_PORT, DEFAULT_REDIS_SENTINEL_PORT,
                DEFAULT_REDIS_SENTINEL_PORT + 1, DEFAULT_REDIS_SENTINEL_PORT +2);
    }

    public RedisSentinel(Integer masterPort, Integer... sentinelPorts) {
        this(new RedisConfig.ServerBuilder(masterPort).build(),
                Collections.singletonList(new RedisConfig.ServerBuilder(masterPort + 1).masterPort(masterPort).build()),
                Arrays.stream(sentinelPorts)
                        .map(sentinelPort -> new RedisConfig.SentinelBuilder(sentinelPort, masterPort).build())
                        .collect(Collectors.toList()));
    }

    public RedisSentinel(RedisConfig masterConfig, List<RedisConfig> slaveConfigs, List<RedisConfig> sentinelConfigs) {
        masterSlave = new RedisMasterSlave(masterConfig, slaveConfigs);
        sentinelConfigs.forEach(sentinelConfig -> sentinels.add(new RedisServer(sentinelConfig)));
    }

    @Override
    public void start() {
        masterSlave.start();
        sentinels.forEach(RedisServer::start);
    }

    @Override
    public void stop() {
        masterSlave.stop();
        sentinels.forEach(RedisServer::stop);
    }

    @Override
    public Boolean isActive() {
        return masterSlave != null && !sentinels.isEmpty() && masterSlave.isActive()
                && sentinels.stream().allMatch(RedisServer::isActive);
    }
}
