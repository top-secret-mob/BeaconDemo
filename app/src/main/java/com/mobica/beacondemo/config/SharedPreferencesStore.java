package com.mobica.beacondemo.config;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Shared preferences based store
 */
class SharedPreferencesStore implements IStore {
    private final SharedPreferences preferences;

    public SharedPreferencesStore(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean contains(String key) {
        return preferences.contains(key);
    }

    @Override
    public void writeBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    @Override
    public boolean readBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    @Override
    public void writeStringSet(String key, Set<String> values) {
        preferences.edit().putStringSet(key, values);
    }

    @Override
    public Set<String> readStringSet(String key) {
        return preferences.getStringSet(key, new HashSet<String>());
    }
}
