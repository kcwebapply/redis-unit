package net.ishiis;

import org.junit.rules.ExternalResource;


public class RedisServerRule extends ExternalResource {
    private final RedisServer redisServer;

    public RedisServerRule() {
        this.redisServer = new RedisServer();
    }

    public RedisServerRule(Integer port) {
        this.redisServer = new RedisServer(port);
    }

    @Override
    protected void before() {
        redisServer.start();
    }

    @Override
    protected void after() {
        redisServer.stop();
    }
}
