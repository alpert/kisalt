package com.github.alpert.repositories;

import com.github.alpert.entities.Url;

public interface DataStore {

    Url get(String id);

    void save(String id, Url url);
}
