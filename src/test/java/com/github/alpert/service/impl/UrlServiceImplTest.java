package com.github.alpert.service.impl;

import com.github.alpert.AbstractTest;
import com.github.alpert.entities.Url;
import com.github.alpert.exception.MalformedUrlException;
import com.github.alpert.exception.UrlCorruptedException;
import com.github.alpert.exception.UrlNotFoundException;
import com.github.alpert.repositories.DataStore;
import com.github.alpert.service.generator.UniqueIdGeneratorService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class UrlServiceImplTest extends AbstractTest {

    @MockBean
    private DataStore                dataStore;
    @MockBean(name = "redisSequenceBasedUniqueIdGenerator")
    private UniqueIdGeneratorService uniqueIdGeneratorService;

    @Value("${kisalt.domain}")
    private String domain;
    @Value("${kisalt.api.version}")
    private String apiVersion;
    @Value("${local.server.port}")
    private String port;

    @Autowired
    UrlServiceImpl urlService;

    @Test
    public void return_existent_url() {
        String id = "a1d";
        Url url = new Url(id, "", "", System.currentTimeMillis());

        given(dataStore.get(id)).willReturn(url);

        assertThat(urlService.getUrl(id)).isEqualTo(url);
    }

    @Test(expected = UrlNotFoundException.class)
    public void exception_for_non_existent_url() {
        String id = "a1d";

        given(dataStore.get(id)).willReturn(null);

        urlService.getUrl(id);
    }

    @Test
    public void create_uri_from_url() throws URISyntaxException {
        Url url = new Url("", "", "http://www.google.com", System.currentTimeMillis());

        assertThat(urlService.createUri(url)).isEqualTo(new URI("http://www.google.com"));
    }

    @Test(expected = UrlCorruptedException.class)
    public void create_uri_from_malformed_url() {
        Url url = new Url("", "", "malformed uri", System.currentTimeMillis());

        urlService.createUri(url);
    }

    @Test
    public void create_url_from_url_string() throws Exception {
        String originalUrl = "http://www.google.com";

        given(uniqueIdGeneratorService.generateUniqueIdForUrl(originalUrl)).willReturn("a1d");

        Method method = urlService.getClass().getDeclaredMethod("getShortUrl",
                                                                String.class,
                                                                String.class,
                                                                String.class,
                                                                String.class);
        method.setAccessible(true);
        Object shortUrl = method.invoke(urlService, domain, port, apiVersion, "a1d");

        Url url = new Url("a1d", String.valueOf(shortUrl), "http://www.google.com", System.currentTimeMillis());

        Url urlExpected = urlService.createUrl(originalUrl);
        assertThat(urlExpected.getId()).isEqualTo(url.getId());
        assertThat(urlExpected.getShortUrl()).isEqualTo(url.getShortUrl());
        assertThat(urlExpected.getOriginalUrl()).isEqualTo(url.getOriginalUrl());
    }

    @Test(expected = MalformedUrlException.class)
    public void create_url_from_malformed_url_string() {
        String originalUrl = "www.google.com";

        urlService.createUrl(originalUrl);
    }
}
