package com.mobica.beacondemo.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Memory based store
 */
class MemoryStore implements IStore {
    private final Map<String, Boolean> boolParams = new ConcurrentHashMap<>();

    @Override
    public boolean contains(String key) {
        return boolParams.containsKey(key);
    }

    @Override
    public void writeBoolean(String key, boolean value) {
        this.boolParams.put(key, value);
    }

    @Override
    public boolean readBoolean(String key, boolean defaultValue) {
        return contains(key) ? this.boolParams.get(key) : defaultValue;
    }

    @Override
    public void writeStringSet(String key, Set<String> values) {
    }

    @Override
    public Set<String> readStringSet(String key) {
        return null;
    }
}
