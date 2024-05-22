package com.example.cache;

import org.springframework.data.redis.connection.jedis.JedisConnection;

/**
 * @author roc
 * @date 2024/5/22 15:43
 */
public interface JedisCallback {
    Object doWithRedis(JedisConnection jedisConnection);
}
