package com.github.alpert.service.generator.impl;

import com.github.alpert.controller.UrlController;
import com.github.alpert.service.generator.UniqueIdGeneratorService;
import com.github.alpert.utils.hash.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class generates unique ids by incrementing a Redis value and encoding it Base62 will more
 * appropriate for using as a short url.
 *
 * It also pre-generates some amount of keys and in an scheduled manner checking if the generated
 * id pool is above a pre-defined limit.
 *
 * Downside of that generator is that if an application is down we will lost some amount of
 * possible ids.
 */
@Component("redisSequenceBasedUniqueIdGenerator")
public class RedisSequenceBasedUniqueIdGenerator implements UniqueIdGeneratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

    private final RedisTemplate redisTemplate;
    private final Queue<String> idQueue = new ConcurrentLinkedQueue<>();

    @Value("${kisalt.redis.sequence}")
    private String sequenceName;

    @Autowired
    public RedisSequenceBasedUniqueIdGenerator(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateUniqueIdForUrl(final String url) {
        final String id = idQueue.poll();
        return id != null ? id : generateId();
    }

    @Scheduled(fixedDelay = 5000)
    public void fillQueue() {
        if (idQueue.size() < 10000) {
            LOGGER.info("Unique id size below {}. Generating new ids.", 10000);

            int i;
            for (i = 0; i < 20000; i++) {
                idQueue.add(generateId());
            }

            LOGGER.info("{} new unique ids has been created.", i);
        }
    }

    @SuppressWarnings("unchecked")
    private String generateId() {
        final Long seq = redisTemplate.opsForValue().increment(sequenceName, 1L);
        assert seq != null;
        return Utils.base62(seq.intValue());
    }
}
