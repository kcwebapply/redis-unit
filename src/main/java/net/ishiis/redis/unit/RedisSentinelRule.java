package net.ishiis.redis.unit;

import org.junit.rules.ExternalResource;


public class RedisSentinelRule extends ExternalResource {
    private final RedisSentinel redisSentinel;

    public RedisSentinelRule() {
        this.redisSentinel = new RedisSentinel();
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
