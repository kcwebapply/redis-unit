package net.ishiis.redis.unit;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisServerRuleTest {

    @Rule
    public RedisServerRule redisServerRule = new RedisServerRule(6333);

    @ClassRule
    public static RedisServerRule redisServerClassRule = new RedisServerRule(7778);

    @Test
    public void testRedisServerRuleSpecifyPort() {
        Jedis jedis = new Jedis("localhost", 6333, 2000, 2000);
        Assert.assertNotNull(jedis.info());
        jedis.close();
    }

    @Test
    public void testRedisServerClassRuleHasState1() {
        Jedis jedis = new Jedis("localhost", 7778);

        if (jedis.get("testKey") == null) {
            jedis.set("testKey", "testValue");
        } else {
            Assert.assertEquals("testValue", jedis.get("testKey"));
        }

        jedis.close();
    }

    @Test
    public void testRedisServerClassRuleHasState2() {
        Jedis jedis = new Jedis("localhost", 7778);

        if (jedis.get("testKey") == null) {
            jedis.set("testKey", "testValue");
        } else {
            Assert.assertEquals("testValue", jedis.get("testKey"));
        }

        jedis.close();
    }

}
