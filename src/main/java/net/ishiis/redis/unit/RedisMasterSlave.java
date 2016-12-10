package net.ishiis.redis.unit;

import net.ishiis.redis.unit.config.RedisMasterSlaveConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.ishiis.redis.unit.config.RedisServerConfig.DEFAULT_REDIS_SERVER_PORT;

public class RedisMasterSlave implements Redis {
    public static final Path WORKING_DIRECTORY = Paths.get(System.getProperty("user.dir"), ".redis", String.valueOf(System.currentTimeMillis()));

    private final RedisServer master;
    private final List<RedisServer> slaves = new ArrayList<>();

    public RedisMasterSlave() {
        this(DEFAULT_REDIS_SERVER_PORT, DEFAULT_REDIS_SERVER_PORT + 1);
    }

    public RedisMasterSlave(Integer masterPort, Integer... slavePorts) {
        this(new RedisMasterSlaveConfig.MasterBuilder(masterPort).build(),
                Arrays.stream(slavePorts)
                        .map(slavePort -> new RedisMasterSlaveConfig
                                .SlaveBuilder(slavePort, masterPort).build()).collect(Collectors.toList()));
    }

    public RedisMasterSlave(RedisMasterSlaveConfig masterConfig, List<RedisMasterSlaveConfig> slaveConfigs) {
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
    public Boolean isActive() {
        return master != null && !slaves.isEmpty() && master.isActive() && slaves.stream().allMatch(RedisServer::isActive);
    }

}
