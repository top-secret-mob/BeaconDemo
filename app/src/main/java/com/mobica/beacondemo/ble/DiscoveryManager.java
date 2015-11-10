package com.mobica.beacondemo.ble;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mobica.beacondemo.config.ConfigStorage;
import com.mobica.beacondemo.geofence.GeofenceProvider;
import com.mobica.beacondemo.wifi.ScannerClient;
import com.mobica.beacondemo.wifi.WifiScanner;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages the state of different device discovery modes
 */
@Singleton
public class DiscoveryManager {
    private static final String TAG = DiscoveryManager.class.getSimpleName();

    @Inject
    Context context;
    @Inject
    WifiScanner wifiScanner;
    @Inject
    ScannerClient scannerClient;
    @Inject
    GeofenceProvider geofenceProvider;

    public void updateModes(EnumSet<DiscoveryMode> activeModes) {
        boolean autoMode = ConfigStorage.bleAutoModeEnabled.get();
        switchWifiActiveMode(autoMode && activeModes.contains(DiscoveryMode.WIFI_ACTIVE));

        if (checkPlayServices()) {
            switchGeoFencingMode(autoMode && activeModes.contains(DiscoveryMode.GEOFENCING));
            switchWifiPassiveMode(autoMode && activeModes.contains(DiscoveryMode.WIFI_PASSIVE));
        } else {
            Log.d(TAG, "Disabling geofencing and wifi passive mode due to lack of google play services");
        }
    }

    private void switchGeoFencingMode(boolean enabled) {
        if (enabled && geofenceProvider.getState() == GeofenceProvider.State.DISCONNECTED) {
            Log.d(TAG, "Enabling geofencing");
            geofenceProvider.connect();
        } else if (!enabled && geofenceProvider.getState() != GeofenceProvider.State.DISCONNECTED) {
            Log.d(TAG, "Disabling geofencing");
            geofenceProvider.disconnect();
        }
    }

    private void switchWifiPassiveMode(boolean enabled) {
        if (enabled && scannerClient.getState() == ScannerClient.State.UNREGISTERED) {
            Log.d(TAG, "Enabling wifi passive scanning");
            scannerClient.register();
        } else if (!enabled && scannerClient.getState() == ScannerClient.State.REGISTERED) {
            Log.d(TAG, "Disabling wifi passive scanning");
            scannerClient.unregister();
        }
    }

    private void switchWifiActiveMode(boolean enabled) {
        if (enabled && !wifiScanner.isActive()) {
            Log.d(TAG, "Enabling wifi active scanning");
            wifiScanner.startScanner();
        } else if (!enabled && wifiScanner.isActive()) {
            Log.d(TAG, "Disabling wifi active scanning");
            wifiScanner.stopScanner();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.i(TAG, "Google play services are not available on this device.");
            return false;
        }
        return true;
    }
}
