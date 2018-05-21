package com.github.alpert.generator.impl;

import com.github.alpert.AbstractTest;
import com.github.alpert.entities.Url;
import com.github.alpert.repositories.DataStore;
import com.github.alpert.service.generator.impl.HashBasedUniqueIdGenerator;
import com.github.alpert.utils.hash.HashProvider;
import com.github.alpert.utils.hash.impl.MurmurHashProvider;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class HashBasedUniqueIdGeneratorTest extends AbstractTest {

    @MockBean
    private DataStore    dataStore;
    @MockBean(name = "murmur3HashProvider")
    private HashProvider hashProvider;

    @Autowired
    HashBasedUniqueIdGenerator hashBasedUniqueIdGenerator;

    @Test
    public void success_on_unique_hash() {
        String originalUrl = "http://www.google.com";
        String hash = new MurmurHashProvider().hash(originalUrl);

        given(hashProvider.hash(originalUrl)).willReturn(hash);
        given(dataStore.get(hash)).willReturn(null);

        assertThat(hashBasedUniqueIdGenerator.generateUniqueIdForUrl(originalUrl)).isEqualTo(hash);
    }

    @Test
    public void rehash_if_conflict() {
        String originalUrl = "http://www.google.com";
        String hash = new MurmurHashProvider().hash(originalUrl);
        Url url = new Url("", "", "", System.currentTimeMillis());

        given(hashProvider.hash(originalUrl)).willReturn(hash);
        given(dataStore.get(hash)).willReturn(url);

        String actual = hashBasedUniqueIdGenerator.generateUniqueIdForUrl(originalUrl);
        assertThat(actual).isNotEqualTo(hash);
    }

    @Test
    public void success_if_already_exists() {
        String originalUrl = "http://www.google.com";
        String hash = new MurmurHashProvider().hash(originalUrl);
        Url url = new Url("", "", originalUrl, System.currentTimeMillis());

        given(hashProvider.hash(originalUrl)).willReturn(hash);
        given(dataStore.get(hash)).willReturn(url);

        assertThat(hashBasedUniqueIdGenerator.generateUniqueIdForUrl(originalUrl)).isEqualTo(hash);
    }
}
