package net.ishiis.redis.unit;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static net.ishiis.redis.unit.config.RedisClusterConfig.DEFAULT_REDIS_CLUSTER_PORT;


public class RedisClusterTest {
    @Test
    public void testStartAndStop() throws InterruptedException {
        RedisCluster redisCluster = new RedisCluster();
        redisCluster.start();

        Jedis jedis = new Jedis("127.0.0.1", DEFAULT_REDIS_CLUSTER_PORT);
        Thread.sleep(2000L);

        Assert.assertNotNull(jedis.clusterInfo());
        Assert.assertNotNull(jedis.clusterInfo().contains("cluster_state:ok"));
        Assert.assertNotNull(jedis.clusterInfo().contains("cluster_known_nodes:3"));
        Assert.assertNotNull(jedis.clusterInfo().contains("cluster_size:3"));

        jedis.close();
        redisCluster.stop();
        Thread.sleep(1000L);
    }

    @Test
    public void testSetValue() throws InterruptedException, IOException {
        RedisCluster redisCluster = new RedisCluster();
        redisCluster.start();
        Thread.sleep(2000L);

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", DEFAULT_REDIS_CLUSTER_PORT));
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", DEFAULT_REDIS_CLUSTER_PORT + 1));
        jedisClusterNodes.add(new HostAndPort("127.0.0.1", DEFAULT_REDIS_CLUSTER_PORT + 2));
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNodes);
        jedisCluster.set("foo", "bar");
        Assert.assertEquals("bar", jedisCluster.get("foo"));

        jedisCluster.close();
        redisCluster.stop();
        Thread.sleep(1000L);
    }
}
