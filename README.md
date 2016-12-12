Redis Unit
=================

[![Build Status](https://travis-ci.org/ishiis/redis-unit.svg?branch=master)](https://travis-ci.org/ishiis/redis-unit)
[![Build Status](https://ci.appveyor.com/api/projects/status/github/ishiis/redis-unit?branch=master&svg=true)](https://ci.appveyor.com/project/ishiis/redis-unit)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.ishiis.redis/redis-unit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.ishiis.redis/redis-unit)

Start and stop Redis(v3.2.5) instance for unit testing applications.

Redis unit requires Java 8 at runtime.

## Features ##
* Redis Server support
* Redis Master / Slave support
* Redis Sentinel (+ Redis Master / Slave) support
* Redis Cluster support

## Maven Central ##
```xml
<dependency>
  <groupId>net.ishiis.redis</groupId>
  <artifactId>redis-unit</artifactId>
  <version>0.0.2</version>
  <scope>test</scope>
</dependency>
```

## Usage ##

### Quick start ###
#### 1, Redis Server ####

Redis Server listen `6379`

```java
RedisServer server = new RedisServer();
server.start();
// do something
server.stop();
```

#### 2, Redis Master / Slave ####

Redis Master listen `6379`, and Slave listen `6380`.
```java
RedisMasterSlave masterSlave = new RedisMasterSlave();
masterSlave.start();
// do something
masterSlave.stop();
```

#### 3, Redis Sentinel (+ Redis Master / Slave) ####

Redis Sentinel listen `26379, 26380, 26381`.

```java
RedisSentinel sentinel = new RedisSentinel();
sentinel.start();
// do something
sentinel.stop();
```

#### 4, Redis Cluster ####

Redis Cluster listen `6379, 6380, 6381`.

```java
RedisCluster cluster = new RedisCluster();
cluster.start();
// do something
cluster.stop();
```

### Customize ###
If you want to customize Redis instances, use `RedisConfig`.

```java
RedisMasterSlaveConfig master = new RedisMasterSlaveConfig.MasterBuilder(6379)
                                        .redisBinaryPath("/usr/local/bin/redis-server").build();
List<RedisMasterSlaveConfig> slaves = new ArrayList<>();
slaves.add(new RedisMasterSlaveConfig.SlaveBuilder(6380, 6379).redisBinaryPath("/usr/local/bin/redis-server").build());
slaves.add(new RedisMasterSlaveConfig.SlaveBuilder(6381, 6379).redisBinaryPath("/usr/local/bin/redis-server").build());

RedisMasterSlave masterSlave = new RedisMasterSlave(master, slaves);
```

## Development
### Requirements ###

* Java 8
* Maven 3.0.0 or higher

### First bootstrap and download the wrapper ###
```
cd redis_unit_source_dir
./mvnw
```

### Running unit tests ###
```
./mvnw test
```

