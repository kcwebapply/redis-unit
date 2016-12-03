package net.ishiis.redis.unit;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static net.ishiis.redis.unit.RedisServer.DEFAULT_REDIS_SERVER_PORT;

public class RedisServerRuleTest {

    @Rule
    public RedisServerRule redisServerRule = new RedisServerRule();

    @Rule
    public RedisServerRule redisServerRule6380 = new RedisServerRule(6380);

    @ClassRule
    public static RedisServerRule redisServerClassRule = new RedisServerRule(6381);

    private Jedis jedis;

    @Test
    public void testRedisServerRule() {
        jedis = new Jedis("localhost", DEFAULT_REDIS_SERVER_PORT, 2000, 2000);
        Assert.assertNotNull(jedis.info());
        jedis.close();
    }

    @Test
    public void testRedisServerRuleSpecifyPort() {
        jedis = new Jedis("localhost", 6380, 2000, 2000);
        Assert.assertNotNull(jedis.info());
        jedis.close();
    }

    @Test
    public void testRedisServerClassRule() {
        jedis = new Jedis("localhost", 6381, 2000, 2000);
        Assert.assertNotNull(jedis.info());
        jedis.close();
    }

}
