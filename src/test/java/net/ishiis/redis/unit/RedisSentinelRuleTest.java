package net.ishiis.redis.unit;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RedisSentinelRuleTest {

    @Rule
    public RedisSentinelRule redisSentinelRule = new RedisSentinelRule(new RedisConfig.ServerBuilder(8888).build(),
            Arrays.asList(new RedisConfig.ServerBuilder(8889).masterPort(8888).build()),
            Arrays.asList(new RedisConfig.SentinelBuilder(18888, 8888).build(),
                    new RedisConfig.SentinelBuilder(18889, 8888).build(),
                    new RedisConfig.SentinelBuilder(18890, 8888).build()));

    @Test(timeout = 15000L)
    public void testRedisSentinelRule() throws InterruptedException {
        Thread.sleep(2000L);

        Jedis connectToSentinel = new Jedis("127.0.0.1", 18888);
        Assert.assertNotNull(connectToSentinel.info("Sentinel"));
        Assert.assertTrue(connectToSentinel.info("Sentinel").contains("slaves=1"));
        Assert.assertTrue(connectToSentinel.info("Sentinel").contains("sentinels=3"));
        connectToSentinel.close();

        Set<String> sentinels = new HashSet<>();
        sentinels.add(new HostAndPort("127.0.0.1", 18888).toString());
        sentinels.add(new HostAndPort("127.0.0.1", 18889).toString());
        sentinels.add(new HostAndPort("127.0.0.1", 18890).toString());

        JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", sentinels);
        Jedis connectToMaster = sentinelPool.getResource();

        Assert.assertNotNull(connectToMaster.info());
        Assert.assertTrue(connectToMaster.info("Replication").contains("role:master"));
        connectToMaster.close();
        sentinelPool.close();
    }

}
