package net.ishiis.redis.unit;


import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static net.ishiis.redis.unit.RedisServer.DEFAULT_REDIS_SERVER_PORT;

public class RedisMasterSlaveTest {

    @Test
    public void testStartAnStop() {
        RedisMasterSlave redisMasterSlave = new RedisMasterSlave();
        redisMasterSlave.start();

        Jedis master = new Jedis("localhost", DEFAULT_REDIS_SERVER_PORT);
        Assert.assertTrue(master.info("Replication").contains("role:master"));
        master.close();

        Jedis slave = new Jedis("localhost", DEFAULT_REDIS_SERVER_PORT + 1);
        Assert.assertTrue(slave.info("Replication").contains("role:slave"));
        Assert.assertTrue(slave.info("Replication").contains("master_host:127.0.0.1"));
        Assert.assertTrue(slave.info("Replication").contains("master_port:6379"));
        slave.close();

        redisMasterSlave.stop();
    }

    @Test
    public void testIsActive() {
        RedisMasterSlave redisMasterSlave = new RedisMasterSlave();
        Assert.assertFalse(redisMasterSlave.isActive());
        redisMasterSlave.start();
        Assert.assertTrue(redisMasterSlave.isActive());
        redisMasterSlave.stop();
        Assert.assertFalse(redisMasterSlave.isActive());
    }
}
