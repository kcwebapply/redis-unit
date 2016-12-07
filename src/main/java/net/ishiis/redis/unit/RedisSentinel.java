package net.ishiis.redis.unit;


import net.ishiis.redis.unit.config.RedisConfig;
import net.ishiis.redis.unit.config.RedisMasterSlaveConfig;
import net.ishiis.redis.unit.config.RedisSentinelConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.ishiis.redis.unit.config.RedisSentinelConfig.DEFAULT_REDIS_SENTINEL_PORT;
import static net.ishiis.redis.unit.config.RedisServerConfig.DEFAULT_REDIS_SERVER_PORT;

public class RedisSentinel implements Redis {

    private final RedisMasterSlave masterSlave;
    private final List<RedisServer> sentinels = new ArrayList<>();

    public RedisSentinel() {
        this(DEFAULT_REDIS_SENTINEL_PORT, DEFAULT_REDIS_SENTINEL_PORT + 1, DEFAULT_REDIS_SENTINEL_PORT +2);
    }

    public RedisSentinel(Integer... sentinelPorts) {
        this(new RedisMasterSlaveConfig.MasterBuilder(DEFAULT_REDIS_SERVER_PORT).build(),
                Collections.singletonList(new RedisMasterSlaveConfig
                        .SlaveBuilder(DEFAULT_REDIS_SERVER_PORT + 1, DEFAULT_REDIS_SERVER_PORT).build()),
                Arrays.stream(sentinelPorts)
                        .map(sentinelPort ->
                                new RedisSentinelConfig.SentinelBuilder(sentinelPort, DEFAULT_REDIS_SERVER_PORT).build())
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
