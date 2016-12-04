package net.ishiis.redis.unit;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static net.ishiis.redis.unit.RedisServer.DEFAULT_REDIS_SERVER_PORT;


public class RedisServerTest {

    @Test
    public void testStartAndStop() {
        RedisServer redisServer = new RedisServer();
        redisServer.start();

        Jedis jedis = new Jedis("127.0.0.1", DEFAULT_REDIS_SERVER_PORT);
        Assert.assertNotNull(jedis.info());
        jedis.close();

        redisServer.stop();
    }

    @Test
    public void testSpecifyPort() {
        RedisServer redisServer = new RedisServer(6380);
        redisServer.start();
        Assert.assertTrue(redisServer.isActive());

        Jedis jedis = new Jedis("127.0.0.1", 6380);
        Assert.assertNotNull(jedis.info());
        jedis.close();

        redisServer.stop();
        Assert.assertFalse(redisServer.isActive());
    }

    @Test
    public void testSetValue() {
        RedisServer redisServer = new RedisServer();
        redisServer.start();

        Jedis jedis = new Jedis("127.0.0.1", DEFAULT_REDIS_SERVER_PORT);
        jedis.set("testKey", "testValue");
        Assert.assertEquals("testValue", jedis.get("testKey"));
        jedis.close();

        redisServer.stop();
    }

    @Test
    public void testIsAlive() {
        RedisServer redisServer = new RedisServer();
        Assert.assertFalse(redisServer.isActive());
        redisServer.start();
        Assert.assertTrue(redisServer.isActive());
        redisServer.stop();
        Assert.assertFalse(redisServer.isActive());
    }

    @Test
    public void testSimpleRunThreeTimes() {
        RedisServer redisServer = new RedisServer();
        Assert.assertFalse(redisServer.isActive());
        redisServer.start();
        Assert.assertTrue(redisServer.isActive());
        redisServer.stop();
        Assert.assertFalse(redisServer.isActive());

        redisServer.start();
        Assert.assertTrue(redisServer.isActive());
        redisServer.stop();
        Assert.assertFalse(redisServer.isActive());

        redisServer.start();
        Assert.assertTrue(redisServer.isActive());
        redisServer.stop();
        Assert.assertFalse(redisServer.isActive());
    }

    @Test
    public void testUsingRedisConfig() {
        RedisServer redisServer = new RedisServer(new RedisConfig.ServerBuilder(6666).build());
        redisServer.start();
        Assert.assertTrue(redisServer.isActive());
        redisServer.stop();
    }

}
