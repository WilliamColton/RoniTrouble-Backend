package org.roni.ronitrouble.component.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public abstract class StringCache {

    private final String cachePrefix = this.getClass().getName() + ":";
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    protected String getValue(String key) {
        return redisTemplate.opsForValue().get(cachePrefix + key);
    }

    protected void removeValue(String key) {
        redisTemplate.delete(cachePrefix + key);
    }

    protected void setValue(String key, String value) {
        redisTemplate.opsForValue().set(cachePrefix + key, value);
    }

    protected void setValue(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(cachePrefix + key, value, timeout, unit);
    }

}
