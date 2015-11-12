package com.mobica.beacondemo.ble;

import android.content.Context;
import android.util.Log;

import com.mobica.beacondemo.R;
import com.mobica.beacondemo.config.ConfigStorage;
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

        boolean autoMode = ConfigStorage.bleAutoModeEnabled.get();
        if (client != null) {
            client.disconnect();
        }

        if (autoMode && !activeModes.isEmpty()) {
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
        }
    }
//
//    private void switchGeoFencingMode(boolean enabled) {
//        if (enabled && geofenceProvider.getState() == GeofenceProvider.State.DISCONNECTED) {
//            Log.d(TAG, "Enabling geofencing");
//            geofenceProvider.connect();
//        } else if (!enabled && geofenceProvider.getState() != GeofenceProvider.State.DISCONNECTED) {
//            Log.d(TAG, "Disabling geofencing");
//            geofenceProvider.disconnect();
//        }
//    }
//
//    private void switchWifiPassiveMode(boolean enabled) {
//        if (enabled && scannerClient.getState() == ScannerClient.State.UNREGISTERED) {
//            Log.d(TAG, "Enabling wifi passive scanning");
//            scannerClient.register();
//        } else if (!enabled && scannerClient.getState() == ScannerClient.State.REGISTERED) {
//            Log.d(TAG, "Disabling wifi passive scanning");
//            scannerClient.unregister();
//        }
//    }
//
//    private void switchWifiActiveMode(boolean enabled) {
//        if (enabled && !wifiScanner.isActive()) {
//            Log.d(TAG, "Enabling wifi active scanning");
//            wifiScanner.startScanner();
//        } else if (!enabled && wifiScanner.isActive()) {
//            Log.d(TAG, "Disabling wifi active scanning");
//            wifiScanner.stopScanner();
//        }
//    }
//
//    private boolean checkPlayServices() {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            Log.i(TAG, "Google play services are not available on this device.");
//            return false;
//        }
//        return true;
//    }


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
