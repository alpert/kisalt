package com.github.alpert.service;

import com.github.alpert.entities.Url;

import java.net.URI;

public interface UrlService {
    Url getUrl(String id);

    URI createUri(Url url);

    Url createUrl(String originalUrl);

    void saveUrl(Url url);
}
