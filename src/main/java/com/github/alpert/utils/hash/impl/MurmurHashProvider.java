package com.github.alpert.utils.hash.impl;

import com.google.common.hash.Hashing;

import com.github.alpert.utils.hash.HashProvider;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component("murmur3HashProvider")
public class MurmurHashProvider implements HashProvider {

    @Override
    public String hash(final String value) {
        return Hashing.murmur3_32().hashString(value, StandardCharsets.UTF_8).toString();
    }
}
