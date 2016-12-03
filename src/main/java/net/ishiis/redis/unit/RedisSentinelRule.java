package net.ishiis.redis.unit;

import org.junit.rules.ExternalResource;

import java.util.List;


public class RedisSentinelRule extends ExternalResource {
    private final RedisSentinel redisSentinel;

    public RedisSentinelRule() {
        this.redisSentinel = new RedisSentinel();
    }

    public RedisSentinelRule(Integer... sentinelPorts) {
        this.redisSentinel = new RedisSentinel(sentinelPorts);
    }

    public RedisSentinelRule(RedisConfig masterConfig, List<RedisConfig> slaveConfigs, List<RedisConfig> sentinelConfigs) {
        this.redisSentinel = new RedisSentinel(masterConfig, slaveConfigs, sentinelConfigs);
    }

    @Override
    protected void before() {
        redisSentinel.start();
    }

    @Override
    protected void after() {
        redisSentinel.stop();
    }

}
