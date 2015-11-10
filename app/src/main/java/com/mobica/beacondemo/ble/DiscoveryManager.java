package com.mobica.beacondemo.ble;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mobica.beacondemo.BeaconApplication;
import com.mobica.beacondemo.config.ConfigStorage;
import com.mobica.beacondemo.gcm.RegistrationIntentService;
import com.mobica.beacondemo.geofence.GeofenceProvider;
import com.mobica.beacondemo.wifi.WifiScanner;

import java.util.EnumSet;

/**
 * Manages the state of different device discovery modes
 */
public class DiscoveryManager {
    private static final String TAG = DiscoveryManager.class.getSimpleName();
    private GeofenceProvider geofenceProvider;
    private WifiScanner wifiScanner;

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
        if (enabled && geofenceProvider == null) {
            geofenceProvider = new GeofenceProvider(BeaconApplication.getAppContext());
            geofenceProvider.connect();
        } else if (!enabled && geofenceProvider != null) {
            geofenceProvider.disconnect();
            geofenceProvider = null;
        }
    }

    private void switchWifiPassiveMode(boolean enabled) {
        final Context context = BeaconApplication.getAppContext();
        if (enabled && !ConfigStorage.registrationPerformed.get()) {
            ConfigStorage.registrationPerformed.set(true);
            final Intent intent = new Intent(context, RegistrationIntentService.class);
            intent.setAction(RegistrationIntentService.ACTION_REGISTER);
            context.startService(intent);
        } else if (!enabled && ConfigStorage.registrationPerformed.get()) {
            ConfigStorage.registrationPerformed.set(false);
            final Intent intent = new Intent(context, RegistrationIntentService.class);
            intent.setAction(RegistrationIntentService.ACTION_UNREGISTER);
            context.startService(intent);
        }
    }

    private void switchWifiActiveMode(boolean enabled) {
        if (enabled && wifiScanner == null) {
            wifiScanner = new WifiScanner();
            wifiScanner.startScanner();
        } else if (!enabled && wifiScanner != null) {
            wifiScanner.stopScanner();
            wifiScanner = null;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(BeaconApplication.getAppContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.i(TAG, "Google play services are not available on this device.");
            return false;
        }
        return true;
    }
}
