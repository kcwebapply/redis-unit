package net.ishiis.redis.unit;

import net.ishiis.redis.unit.config.RedisMasterSlaveConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.ishiis.redis.unit.config.RedisServerConfig.DEFAULT_REDIS_SERVER_PORT;

/**
 * Redis Master Slave
 */
public class RedisMasterSlave implements Redis {
    public static final Path WORKING_DIRECTORY = Paths.get(System.getProperty("user.dir"), ".redis", String.valueOf(System.currentTimeMillis()));

    private final RedisServer master;
    private final List<RedisServer> slaves = new ArrayList<>();

    /**
     * Default constructor
     */
    public RedisMasterSlave() {
        this(DEFAULT_REDIS_SERVER_PORT, DEFAULT_REDIS_SERVER_PORT + 1);
    }

    /**
     * Constructor
     * @param masterPort master port
     * @param slavePorts Array of slave port
     */
    public RedisMasterSlave(Integer masterPort, Integer... slavePorts) {
        this(new RedisMasterSlaveConfig.MasterBuilder(masterPort).build(),
                Arrays.stream(slavePorts)
                        .map(slavePort -> new RedisMasterSlaveConfig
                                .SlaveBuilder(slavePort, masterPort).build()).collect(Collectors.toList()));
    }

    /**
     * Constructor
     * @param masterConfig master configuration
     * @param slaveConfigs slave configuration list
     */
    public RedisMasterSlave(RedisMasterSlaveConfig masterConfig, List<RedisMasterSlaveConfig> slaveConfigs) {
        master = new RedisServer(masterConfig);
        slaveConfigs.forEach(slaveConfig -> slaves.add(new RedisServer(slaveConfig)));
    }

    /**
     * Start master node and slave nodes
     */
    @Override
    public void start() {
        master.start();
        slaves.forEach(RedisServer::start);
    }

    /**
     * Stop master node and slave nodes
     */
    @Override
    public void stop() {
        master.stop();
        slaves.forEach(RedisServer::stop);
    }

    /**
     * Return whether all node is alive
     * @return {@code true} if all node is active, {@code false} otherwise
     */
    @Override
    public Boolean isActive() {
        return master != null && !slaves.isEmpty() && master.isActive() && slaves.stream().allMatch(RedisServer::isActive);
    }

}
