package net.ishiis.redis.unit;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

import static net.ishiis.redis.unit.RedisSentinel.DEFAULT_REDIS_SENTINEL_PORT;

public class RedisSentinelTest {

    @Test
    public void testStartAndStop() throws InterruptedException {
        RedisSentinel redisSentinel = new RedisSentinel();
        redisSentinel.start();

        Set<String> sentinels = new HashSet<>();
        sentinels.add(new HostAndPort("localhost", DEFAULT_REDIS_SENTINEL_PORT).toString());
        sentinels.add(new HostAndPort("localhost", DEFAULT_REDIS_SENTINEL_PORT + 1).toString());
        sentinels.add(new HostAndPort("localhost", DEFAULT_REDIS_SENTINEL_PORT + 2).toString());

        Thread.sleep(1000L);

        Jedis connectToSentinel = new Jedis("localhost", DEFAULT_REDIS_SENTINEL_PORT);
        Assert.assertNotNull(connectToSentinel.info("Sentinel"));
        Assert.assertTrue(connectToSentinel.info("Sentinel").contains("slaves=1"));
        Assert.assertTrue(connectToSentinel.info("Sentinel").contains("sentinels=3"));
        connectToSentinel.close();

        JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", sentinels);
        Jedis connectToMaster = sentinelPool.getResource();

        Assert.assertNotNull(connectToMaster.info());
        Assert.assertTrue(connectToMaster.info("Replication").contains("role:master"));
        connectToMaster.close();
        sentinelPool.close();
        redisSentinel.stop();
    }

    @Test
    public void testSpecifyPort() throws InterruptedException {
        RedisSentinel redisSentinel = new RedisSentinel(36479, 36480, 36481, 36482, 36483);
        redisSentinel.start();

        Thread.sleep(1000L);

        Assert.assertTrue(redisSentinel.isActive());
        redisSentinel.stop();
        Assert.assertFalse(redisSentinel.isActive());
    }

    @Test
    public void testIsActive() throws InterruptedException {
        RedisSentinel redisSentinel = new RedisSentinel();
        redisSentinel.start();

        Thread.sleep(1000L);

        Assert.assertTrue(redisSentinel.isActive());
        redisSentinel.stop();
        Assert.assertFalse(redisSentinel.isActive());
    }

    @Test
    public void testSimpleRunThreeTimes() throws InterruptedException {
        RedisSentinel redisSentinel = new RedisSentinel();
        redisSentinel.start();
        Thread.sleep(1000L);
        Assert.assertTrue(redisSentinel.isActive());
        redisSentinel.stop();
        Assert.assertFalse(redisSentinel.isActive());

        redisSentinel.start();
        Thread.sleep(1000L);
        Assert.assertTrue(redisSentinel.isActive());
        redisSentinel.stop();
        Assert.assertFalse(redisSentinel.isActive());

        redisSentinel.start();
        Thread.sleep(1000L);
        Assert.assertTrue(redisSentinel.isActive());
        redisSentinel.stop();
        Assert.assertFalse(redisSentinel.isActive());
    }

}
