package com.mobica.beacondemo.ble;

import android.content.Context;
import android.util.Log;

import com.mobica.beacondemo.R;
import com.mobica.discoverysdk.DiscoveryClient;
import com.mobica.discoverysdk.DiscoveryMode;
import com.mobica.discoverysdk.gcm.GcmMessageProxy;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages the state of different device discovery modes
 */
@Singleton
public class DiscoveryManager implements DiscoveryClient.DiscoveryClientListener,
        DiscoveryClient.DiscoveryConnectionListener {
    private static final String TAG = DiscoveryManager.class.getSimpleName();

    @Inject
    Context context;
    @Inject
    GcmMessageProxy gcmProxy;
    private DiscoveryClient client;

    public void updateModes(EnumSet<DiscoveryMode> activeModes) {
        Log.d(TAG, "Updating modes to: " + activeModes);

        if (!activeModes.isEmpty()) {
            final DiscoveryClient.Builder builder = new DiscoveryClient.Builder(context)
                    .setDiscoveryConnectionListener(this)
                    .setDiscoveryListener(this);

            if (activeModes.contains(DiscoveryMode.NFC)) {
                builder.addDiscoveryMode(DiscoveryMode.NFC);
            }

            if (activeModes.contains(DiscoveryMode.WIFI_ACTIVE)) {
                builder.addDiscoveryMode(DiscoveryMode.WIFI_ACTIVE)
                        .setWifiScanningFrequency(context.getResources().getInteger(R.integer.wifi_scanning_rate))
                        .setWifiOutOfRangeTimeout(context.getResources().getInteger(R.integer.wifi_lost_timeout))
                        .setWifiSsidPattern(context.getString(R.string.wifi_ssid_pattern));
            }

            if (activeModes.contains(DiscoveryMode.WIFI_PASSIVE)) {
                builder.addDiscoveryMode(DiscoveryMode.WIFI_PASSIVE)
                        .setGcmProxy(gcmProxy);
            }

            if (activeModes.contains(DiscoveryMode.GEOFENCING)) {
                builder.addDiscoveryMode(DiscoveryMode.GEOFENCING);
            }

            client = builder.build();
            client.connect();
        } else if (client != null) {
            client.disconnect();
        }
    }

    @Override
    public void onStoreInRange(DiscoveryMode mode) {
        Log.d(TAG, "Found store (" + mode + ")");
    }

    @Override
    public void onStoreOutOfRange(DiscoveryMode mode) {
        Log.d(TAG, "Store lost (" + mode + ")");
    }

    @Override
    public void onModeEnabled(DiscoveryMode mode) {
        Log.d(TAG, "Mode enabled: " + mode);
    }

    @Override
    public void onModeEnablingFailed(DiscoveryMode mode, String error) {
        Log.d(TAG, "Mode enabling failed: " + mode + " reason: " + error);
    }
}
