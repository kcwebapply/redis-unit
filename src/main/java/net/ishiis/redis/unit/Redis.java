package net.ishiis.redis.unit;

public interface Redis {
    void start();
    void stop();
    Boolean isAlive();
}