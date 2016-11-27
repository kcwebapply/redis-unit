package net.ishiis;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;

import static net.ishiis.RedisServer.DEFAULT_REDIS_SERVER_PORT;


public class RedisServerTest {

    @Test
    public void testStart() {
        RedisServer redisServer = new RedisServer();
        redisServer.start();

        Jedis jedis = new Jedis("localhost", DEFAULT_REDIS_SERVER_PORT);
        Assert.assertNotNull(jedis.info());
        jedis.close();

        redisServer.stop();
    }

    @Test
    public void testSpecifyPort() {
        RedisServer redisServer = new RedisServer(6380);
        redisServer.start();
        Assert.assertTrue(redisServer.isAlive());

        Jedis jedis = new Jedis("localhost", 6380);
        Assert.assertNotNull(jedis.info());
        jedis.close();

        redisServer.stop();
        Assert.assertFalse(redisServer.isAlive());
    }

    @Test
    public void testStop() {
        RedisServer redisServer = new RedisServer();
        redisServer.start();
        Assert.assertTrue(redisServer.isAlive());

        redisServer.stop();
        Assert.assertFalse(redisServer.isAlive());
    }

    @Test
    public void testSetValue() {
        RedisServer redisServer = new RedisServer();
        redisServer.start();

        Jedis jedis = new Jedis("localhost", DEFAULT_REDIS_SERVER_PORT);
        jedis.set("testKey", "testValue");
        Assert.assertEquals("testValue", jedis.get("testKey"));
        jedis.close();

        redisServer.stop();
    }

}
