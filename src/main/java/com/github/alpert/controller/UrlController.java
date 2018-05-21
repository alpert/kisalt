package com.github.alpert.controller;

import com.github.alpert.entities.Url;
import com.github.alpert.service.UrlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import io.swagger.annotations.Api;

@RequestMapping("/v1")
@RestController
@Api("Url endpoints for shortening long URLs.")
public class UrlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping(value = "/{id}")
    ResponseEntity<?> redirect(@PathVariable String id) {
        LOGGER.debug("New redirect request with id: [{}]", id);

        final Url url = urlService.getUrl(id);

        final URI uri = urlService.createUri(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }

    @PostMapping(value = "/shorten")
    ResponseEntity<?> shorten(@RequestBody String originalUrl) {
        LOGGER.debug("New shortening request for URL: [{}]", originalUrl);

        final Url url = urlService.createUrl(originalUrl);
        urlService.saveUrl(url);

        LOGGER.debug("URL [{}] shortened as [{}]", originalUrl, url.getShortUrl());
        return ResponseEntity.ok().body(url);
    }

    @GetMapping(value = "/infos/{id}")
    ResponseEntity<?> getInfos(@PathVariable String id) {
        final Url url = urlService.getUrl(id);
        return ResponseEntity.ok(url);
    }
}