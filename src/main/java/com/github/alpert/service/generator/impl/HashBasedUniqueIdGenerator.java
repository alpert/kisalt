package com.github.alpert.service.generator.impl;

import com.github.alpert.entities.Url;
import com.github.alpert.repositories.DataStore;
import com.github.alpert.service.generator.UniqueIdGeneratorService;
import com.github.alpert.utils.hash.HashProvider;
import com.github.alpert.utils.hash.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This class provides hash based unique id generation capabilities. By default it uses Murmur3 hash
 * function. It is one of the fastest hash functions and have a good collision resistance.
 *
 * Generation an id based on a hash function provides a side feature as it will not generate
 * different ids for same url.
 *
 * At the same time, as that class checking the hash collision through data store its open to
 * concurrency problems. If two different threads or application has a collision at the same time
 * they will both check and put the same value and one of them will be overridden.
 *
 * DON'T USE this generator for serious work. However it can be useful for demo purposes.
 */
@Component
public class HashBasedUniqueIdGenerator implements UniqueIdGeneratorService {

    private final HashProvider hashProvider;
    private final DataStore    dataStore;
    private final Random       random;

    @Autowired
    public HashBasedUniqueIdGenerator(@Qualifier("murmur3HashProvider") HashProvider hashProvider,
                                      DataStore dataStore) {
        this.hashProvider = hashProvider;
        this.dataStore = dataStore;
        this.random = new Random();
    }

    @Override
    public String generateUniqueIdForUrl(final String url) {
        final String hash = hashProvider.hash(url);
        final Url old = dataStore.get(hash);

        if (notSame(url, old))
            return reHash(url, hash);

        return hash;
    }

    private String reHash(final String url, final String hash) {
        return Stream.iterate(hash, iteratedHash -> (iteratedHash + Utils.ALPHABET.charAt(random.nextInt(Utils.ALPHABET_LEN))))
                .filter(isSame(url))
                .findFirst()
                .orElse(hash);
    }

    private boolean notSame(final String url, final Url old) {
        return old != null && !isSame(url, old.getOriginalUrl());
    }

    private Predicate<String> isSame(final String url) {
        return newHash -> {
            final Url old = dataStore.get(newHash);
            return !notSame(url, old);
        };
    }

    private boolean isSame(final String url, final String old) {
        return old == null || old.equals(url);
    }
}
