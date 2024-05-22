Reference：[mybatis-redis-cache](https://mybatis.org/redis-cache/)，the project has not been updated for a long time.

- jedis directory


    Based on the spring-data-redis + Jedis implementation, JedisConnection is a wrapper of Jedis.

- redis-template directory (Recommend)


    Based on the RedisTemplate implementation, this approach is much more flexible.

    It abstracts the underlying Redis connection management through the RedisConnectionFactory, 
    regardless of the specific Redis client.

- Tips

    
    The default serialization of different tool classes is not the same, do not mix.