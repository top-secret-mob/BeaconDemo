package com.mobica.beacondemo.settings;

import com.mobica.discoverysdk.DiscoveryMode;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by woos on 2015-11-13.
 */
public class Mapping {
    private static final Map<String, DiscoveryMode> discoveryModeMap = new HashMap<>();

    static {
        discoveryModeMap.put("0", DiscoveryMode.NFC);
        discoveryModeMap.put("1", DiscoveryMode.GEOFENCING);
        discoveryModeMap.put("2", DiscoveryMode.WIFI_ACTIVE);
        discoveryModeMap.put("3", DiscoveryMode.WIFI_PASSIVE);
    }

    public static Map<String, DiscoveryMode> getDiscoveryModeValueMapping() {
        return discoveryModeMap;
    }

    public static EnumSet<DiscoveryMode> mapDiscoveryModes(Set<String> values) {
        final EnumSet<DiscoveryMode> enumSet = EnumSet.noneOf(DiscoveryMode.class);
        for (String value : values) {
            final DiscoveryMode mode = discoveryModeMap.get(value);
            if (mode != null) {
                enumSet.add(mode);
            }
        }

        return enumSet;
    }
}
