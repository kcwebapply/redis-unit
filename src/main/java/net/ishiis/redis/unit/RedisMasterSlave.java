package net.ishiis.redis.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.ishiis.redis.unit.RedisServer.DEFAULT_REDIS_SERVER_PORT;

public class RedisMasterSlave implements Redis {
    private final RedisServer master;
    private final List<RedisServer> slaves = new ArrayList<>();

    public RedisMasterSlave() {
        this(DEFAULT_REDIS_SERVER_PORT, DEFAULT_REDIS_SERVER_PORT + 1);
    }

    public RedisMasterSlave(Integer masterPort, Integer... slavePorts) {
        this(new RedisConfig.ServerBuilder(masterPort).build(),
                Arrays.stream(slavePorts)
                        .map(slavePort -> new RedisConfig.ServerBuilder(slavePort).masterPort(masterPort).build())
                        .collect(Collectors.toList()));
    }

    public RedisMasterSlave(RedisConfig masterConfig, List<RedisConfig> slaveConfigs) {
        master = new RedisServer(masterConfig);
        slaveConfigs.forEach(slaveConfig -> slaves.add(new RedisServer(slaveConfig)));
    }

    @Override
    public void start() {
        master.start();
        slaves.forEach(RedisServer::start);
    }

    @Override
    public void stop() {
        master.stop();
        slaves.forEach(RedisServer::stop);
    }

    @Override
    public Boolean isAlive() {
        return master != null && !slaves.isEmpty() && master.isAlive() && slaves.stream().allMatch(RedisServer::isAlive);
    }

}
