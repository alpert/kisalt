package com.github.alpert.integration;

import com.github.alpert.service.generator.impl.RedisSequenceBasedUniqueIdGenerator;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.Queue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class RedisSequenceBasedUniqueIdGeneratorTest extends AbstractIntegrationTest {

    @Autowired
    RedisSequenceBasedUniqueIdGenerator generator;

    @Test
    public void not_null() {
        assertThat(generator.generateUniqueIdForUrl("http://www.google.com")).isNotNull();
    }

    @Test
    public void filling_queue() throws Exception {
        Field idQueue = generator.getClass().getDeclaredField("idQueue");
        idQueue.setAccessible(true);

        // wait for scheduled method to run
        SECONDS.sleep(5);

        assertThat(((Queue) idQueue.get(generator)).size()).isGreaterThan(0);
    }

    @Test
    public void ids_are_not_same() {
        String id1 = generator.generateUniqueIdForUrl("http://www.google.com");
        String id2 = generator.generateUniqueIdForUrl("http://www.google.com");
        assertThat(id1).isNotEqualTo(id2);
    }
}
