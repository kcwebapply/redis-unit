package net.ishiis;

public interface Redis {
    void start();
    void stop();
    Boolean isAlive();
}
