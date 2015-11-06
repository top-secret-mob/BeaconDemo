package com.mobica.beacondemo.config;

import java.util.Set;

/**
 * Parameter store abstraction
 */
interface IStore {

    abstract class Parameter<T> {
        protected final IStore store;
        protected final String key;
        protected final T defaultValue;

        public Parameter(IStore store, String key, T defaultValue) {
            this.store = store;
            this.key = key;
            this.defaultValue = defaultValue;
        }

        public abstract T get();

        public abstract void set(T value);
    }

    boolean contains(String key);

    void writeBoolean(String key, boolean value);

    boolean readBoolean(String key, boolean defaultValue);

    void writeStringSet(String key, Set<String> values);

    Set<String> readStringSet(String key);
}
