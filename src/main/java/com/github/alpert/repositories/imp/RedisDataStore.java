package com.github.alpert.repositories.imp;

import com.github.alpert.entities.Url;
import com.github.alpert.repositories.DataStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisDataStore implements DataStore {

    private final RedisTemplate redisTemplate;

    @Autowired
    public RedisDataStore(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Cacheable("urls")
    public Url get(final String id) {
        return (Url) redisTemplate.opsForValue().get(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void save(final String id, final Url url) {
        redisTemplate.opsForValue().set(id, url);
    }
}
