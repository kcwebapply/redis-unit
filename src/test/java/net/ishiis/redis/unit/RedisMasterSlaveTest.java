package net.ishiis.redis.unit;


import org.junit.Assert;
import org.junit.Ignore;
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
    public void testIsAlive() {
        RedisMasterSlave redisMasterSlave = new RedisMasterSlave();
        Assert.assertFalse(redisMasterSlave.isAlive());
        redisMasterSlave.start();
        Assert.assertTrue(redisMasterSlave.isAlive());
        redisMasterSlave.stop();
        Assert.assertFalse(redisMasterSlave.isAlive());
    }
}
