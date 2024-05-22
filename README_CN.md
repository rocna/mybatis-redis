[English](./README.md) / 简体中文

<h1>Mybatis-Redis-Cache</h1>

实现思路参考：[mybatis-redis-cache](https://mybatis.org/redis-cache/)，这个项目好久不更新了。
 
## jedis 目录


    基于 spring-data-redis + Jedis 实现，其中 JedisConnection 是 Jedis 的包装。


## redis-template 目录（推荐）


    基于 RedisTemplate 实现，这种实现方式会更加的简单。

    其通过 RedisConnectionFactory 来抽象底层的 Redis 连接管理，与具体的 Redis 客户端无关。

## 注意


    不同的工具类默认的序列化方式是不一样的，最好不要交叉使用。