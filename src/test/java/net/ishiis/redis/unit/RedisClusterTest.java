package net.ishiis.redis.unit;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static net.ishiis.redis.unit.RedisCluster.DEFAULT_REDIS_CLUSTER_PORT;

public class RedisClusterTest {
    @Test
    public void testStartAndStop() throws InterruptedException {
        RedisCluster redisCluster = new RedisCluster();
        redisCluster.start();

        Jedis jedis = new Jedis("localhost", DEFAULT_REDIS_CLUSTER_PORT);
        Thread.sleep(1000L);

      //  Assert.assertNotNull(jedis.clusterInfo());

        Thread.sleep(1000L);
        jedis.close();
        redisCluster.stop();
    }
}
