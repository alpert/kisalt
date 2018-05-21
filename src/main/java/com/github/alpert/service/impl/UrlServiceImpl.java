package com.github.alpert.service.impl;

import com.github.alpert.entities.Url;
import com.github.alpert.exception.MalformedUrlException;
import com.github.alpert.exception.UrlCorruptedException;
import com.github.alpert.exception.UrlNotFoundException;
import com.github.alpert.repositories.DataStore;
import com.github.alpert.service.UrlService;
import com.github.alpert.service.generator.UniqueIdGeneratorService;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Component

public class UrlServiceImpl implements UrlService {

    private final DataStore                dataStore;
    private final UniqueIdGeneratorService uniqueIdGeneratorService;
    private final Environment              environment;

    @Value("${kisalt.domain}")
    private String domain;
    @Value("${kisalt.api.version}")
    private String apiVersion;

    private final UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

    @Autowired
    public UrlServiceImpl(DataStore dataStore,
                          @Qualifier("redisSequenceBasedUniqueIdGenerator") UniqueIdGeneratorService uniqueIdGeneratorService,
                          Environment environment) {
        this.dataStore = dataStore;
        this.uniqueIdGeneratorService = uniqueIdGeneratorService;
        this.environment = environment;
    }

    @Override
    public Url getUrl(final String id) {
        final Optional<Url> optionalUrl = Optional.ofNullable(dataStore.get(id));
        return optionalUrl
                .orElseThrow(() -> new UrlNotFoundException(String.format("URL for [%s] not found",
                                                                          id)));
    }

    @Override
    public URI createUri(final Url url) {
        try {
            return new URI(url.getOriginalUrl());
        } catch (URISyntaxException e) {
            throw new UrlCorruptedException(String.format("URL for [%s] seems corrupted: [%s]",
                                                          url.getId(),
                                                          url.getOriginalUrl()));
        }
    }

    @Override
    public Url createUrl(final String originalUrl) {
        if (urlValidator.isValid(originalUrl)) {
            final String id = uniqueIdGeneratorService.generateUniqueIdForUrl(originalUrl);
            final String shortUrl = getShortUrl(domain, environment.getProperty("local.server.port"), apiVersion, id);
            return new Url(id, shortUrl, originalUrl, System.currentTimeMillis());
        } else {
            throw new MalformedUrlException(String.format("[%s] is not a valid URL", originalUrl));
        }
    }

    @Override
    public void saveUrl(final Url url) {
        this.dataStore.save(url.getId(), url);
    }

    private String getShortUrl(final String domain,
                               final String port,
                               final String apiVersion,
                               final String id) {
        return domain + ":" + port + apiVersion + "/" + id;
    }
}
