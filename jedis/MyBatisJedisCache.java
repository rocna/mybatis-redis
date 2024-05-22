package com.example.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * MyBatis 二级缓存
 *
 * @author roc
 */
@Slf4j
public class MyBatisJedisCache implements Cache {

    // 读写锁
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    //这里使用了redis缓存，使用springboot自动注入
    private JedisConnectionFactory factory;

    private String id;

    public MyBatisJedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
        factory = SpringContextUtils.getBean("jedisConnectionFactory");
    }

    private Object execute(JedisCallback callback) {
        JedisConnection jedis = factory.getConnection();
        try {
            return callback.doWithRedis(jedis);
        } finally {
            jedis.close();
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getSize() {
        return (Integer) execute(jedisConnection -> {
            Map<byte[], byte[]> result = jedisConnection.hGetAll(id.getBytes());
            return result.size();
        });
    }

    @Override
    public void putObject(final Object key, final Object value) {
        execute(jedisConnection -> {
            jedisConnection.hSet(id.getBytes(), key.toString().getBytes(), SerializeUtil.serialize(value));
            jedisConnection.expire(id.getBytes(), 60 * 60 * 24 * 15);
            return null;
        });
    }

    @Override
    public Object getObject(final Object key) {
        return execute(jedisConnection -> SerializeUtil.unserialize(jedisConnection.hGet(id.getBytes(), key.toString().getBytes())));
    }

    @Override
    public Object removeObject(final Object key) {
        return execute(jedisConnection -> jedisConnection.hDel(id.getBytes(), key.toString().getBytes()));
    }

    @Override
    public void clear() {
        execute(jedis -> {
            jedis.del(id.getBytes());
            return null;
        });

    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

}