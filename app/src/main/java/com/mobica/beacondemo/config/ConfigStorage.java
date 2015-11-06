package com.mobica.beacondemo.config;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.collect.Sets;
import com.mobica.beacondemo.BeaconApplication;
import com.mobica.beacondemo.ble.SwitchMode;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Keeps all persistent and non-persistent configuration parameters
 */
public class ConfigStorage {
    /**
     * Whether BLE adapter was enabled when application started
     */
    public static IStore.Parameter<Boolean> wasBleEnabled;
    /**
     * Whether NFC mode is enabled for BLE switching
     */
    public static IStore.Parameter<EnumSet<SwitchMode>> bleSwitchMode;

    public static void setup() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                BeaconApplication.getAppContext());
        final SharedPreferencesStore sharedPreferencesStore =
                new SharedPreferencesStore(preferences);
        final MemoryStore memoryStore = new MemoryStore();

        wasBleEnabled = new BooleanParam(memoryStore, "wasBleEnabled", false);

        final Map<String, SwitchMode> mapping = new HashMap<>();
        mapping.put("0", SwitchMode.NFC);
        mapping.put("1", SwitchMode.GEOFENCING);
        mapping.put("2", SwitchMode.WIFI_ACTIVE);
        mapping.put("3", SwitchMode.WIFI_PASSIVE);
        bleSwitchMode = new EnumSetParam<>(sharedPreferencesStore, "bt_auto_switch_modes",
                EnumSet.noneOf(SwitchMode.class), SwitchMode.class, mapping);
    }

    /**
     * Boolean parameter
     */
    private static class BooleanParam extends IStore.Parameter<Boolean> {
        public BooleanParam(IStore store, String key, Boolean defaultValue) {
            super(store, key, defaultValue);
        }

        @Override
        public Boolean get() {
            return store.readBoolean(key, defaultValue);
        }

        @Override
        public void set(Boolean value) {
            store.writeBoolean(key, value);
        }
    }

    /**
     * EnumSet parameter
     */
    private static class EnumSetParam<T extends Enum<T>> extends IStore.Parameter<EnumSet<T>> {
        private final Map<String, T> mapping;
        private final Class<T> enumClass;

        public EnumSetParam(IStore store, String key, EnumSet<T> defaultValue,
                            Class<T> enumClass, Map<String, T> mapping) {
            super(store, key, defaultValue);
            this.enumClass = enumClass;
            this.mapping = mapping;
        }

        @Override
        public EnumSet<T> get() {
            final Set<String> values = store.readStringSet(key);
            final EnumSet<T> enumValues = EnumSet.noneOf(enumClass);

            for (String value : values) {
                enumValues.add(mapping.get(value));
            }

            return enumValues;
        }

        @Override
        public void set(EnumSet<T> value) {
            final Set<String> values = Sets.newHashSet();

            for (String key : mapping.keySet()) {
                final T mapValue = mapping.get(key);
                if (value.contains(mapValue)) {
                    values.add(key);
                }
            }

            store.writeStringSet(key, values);
        }
    }
}
