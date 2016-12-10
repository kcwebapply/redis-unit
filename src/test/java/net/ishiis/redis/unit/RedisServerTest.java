package net.ishiis.redis.unit;


import net.ishiis.redis.unit.config.RedisServerConfig;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static net.ishiis.redis.unit.config.RedisServerConfig.DEFAULT_REDIS_SERVER_PORT;


public class RedisServerTest {

    @Test
    public void testStartAndStop() throws InterruptedException {
        RedisServer redisServer = new RedisServer();
        redisServer.start();

        Thread.sleep(1000L);

        Jedis jedis = new Jedis("127.0.0.1", DEFAULT_REDIS_SERVER_PORT);
        Assert.assertNotNull(jedis.info());
        jedis.close();

        redisServer.stop();
        Thread.sleep(1000L);
    }

    @Test
    public void testSpecifyPort() throws InterruptedException {
        RedisServer redisServer = new RedisServer(6380);
        redisServer.start();
        Thread.sleep(1000L);
        Assert.assertTrue(redisServer.isActive());

        Jedis jedis = new Jedis("127.0.0.1", 6380);
        Assert.assertNotNull(jedis.info());
        jedis.close();

        redisServer.stop();
        Thread.sleep(1000L);
        Assert.assertFalse(redisServer.isActive());
    }

    @Test
    public void testSetValue() throws InterruptedException {
        RedisServer redisServer = new RedisServer();
        redisServer.start();

        Thread.sleep(1000L);
        Jedis jedis = new Jedis("127.0.0.1", DEFAULT_REDIS_SERVER_PORT);
        jedis.set("testKey", "testValue");
        Assert.assertEquals("testValue", jedis.get("testKey"));
        jedis.close();

        redisServer.stop();
        Thread.sleep(1000L);
    }

    @Test
    public void testIsAlive() throws InterruptedException {
        RedisServer redisServer = new RedisServer();
        Assert.assertFalse(redisServer.isActive());
        redisServer.start();
        Thread.sleep(1000L);
        Assert.assertTrue(redisServer.isActive());
        redisServer.stop();
        Assert.assertFalse(redisServer.isActive());
        Thread.sleep(1000L);
    }

    @Test
    public void testSimpleRunThreeTimes() throws InterruptedException {
        RedisServer redisServer = new RedisServer();
        Assert.assertFalse(redisServer.isActive());
        redisServer.start();

        Thread.sleep(1000L);
        Assert.assertTrue(redisServer.isActive());
        redisServer.stop();
        Assert.assertFalse(redisServer.isActive());

        Thread.sleep(1000L);
        redisServer.start();
        Assert.assertTrue(redisServer.isActive());

        Thread.sleep(1000L);
        redisServer.stop();
        Assert.assertFalse(redisServer.isActive());

        Thread.sleep(1000L);
        redisServer.start();
        Assert.assertTrue(redisServer.isActive());

        Thread.sleep(1000L);
        redisServer.stop();
        Assert.assertFalse(redisServer.isActive());

        Thread.sleep(1000L);
    }

    @Test
    public void testUsingRedisConfig() throws InterruptedException {
        RedisServer redisServer = new RedisServer(new RedisServerConfig.ServerBuilder(6666).build());
        redisServer.start();
        Thread.sleep(1000L);
        Assert.assertTrue(redisServer.isActive());
        redisServer.stop();
        Thread.sleep(1000L);
    }

}
