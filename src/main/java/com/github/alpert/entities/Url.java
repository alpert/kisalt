package com.github.alpert.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Url implements Serializable {
    private String id;
    private String shortUrl;
    private String originalUrl;
    private Long   createdAt;
}
