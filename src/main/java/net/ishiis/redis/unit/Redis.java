package net.ishiis.redis.unit;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public abstract class Redis {
    private static final String DEFAULT_REDIS_BINARY_NAME = "redis-server.3.2.5";

    abstract public void start();
    abstract public void stop();
    abstract public Boolean isAlive();

    protected String getDefaultRedisBinaryPath() {
        String redisBinaryPath;
        try {
            redisBinaryPath = URLDecoder.decode(
                    this.getClass().getClassLoader().getResource(DEFAULT_REDIS_BINARY_NAME).getPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is not supported Encoding.", e);
        }
        return redisBinaryPath;
    }
}