package com.example.cache;

import com.jielin.interceptor.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * MyBatis 二级缓存
 *
 * @author roc
 */
@Slf4j
public class MyBatisRedisCache implements Cache {

    // 读写锁
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    //这里使用了redis缓存，使用springboot自动注入
    private RedisTemplate<String, Object> redisTemplate;

    private String id;

    public MyBatisRedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
        if (redisTemplate == null) {
            redisTemplate = (RedisTemplate<String, Object>) SpringContextUtils.getBean("redisTemplate");
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        if (value != null) {
            redisTemplate.opsForHash().put(id, key.toString(), value);
            redisTemplate.expire(id, 15, TimeUnit.DAYS);
        }
    }

    @Override
    public Object getObject(Object key) {
        try {
            if (key != null) {
                return redisTemplate.opsForHash().get(id, key.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("缓存出错 ");
        }
        return null;
    }

    @Override
    public Object removeObject(Object key) {
        if (key != null) {
            redisTemplate.opsForHash().delete(id, key.toString());
        }
        return null;
    }

    @Override
    public void clear() {
        log.debug("清空缓存");
        try {
            redisTemplate.delete(id);
        } catch (Exception e) {
            log.error("清空缓存", e);
        }
    }

    @Override
    public int getSize() {
        Long size = redisTemplate.opsForHash().size(id);
        return size.intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

}