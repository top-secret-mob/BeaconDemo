package com.mobica.beacondemo.dagger;

import android.content.Context;

import com.mobica.beacondemo.SettingsActivity;
import com.mobica.beacondemo.ble.DiscoveryManager;
import com.mobica.beacondemo.geofence.GeoFencesFetcherFactory;
import com.mobica.beacondemo.geofence.GeofenceProvider;
import com.mobica.beacondemo.geofence.GoogleApiClientFactory;
import com.mobica.beacondemo.wifi.ScannerClient;
import com.mobica.beacondemo.wifi.WifiScanner;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger main module
 */
@Module (injects = {DiscoveryManager.class, Context.class, WifiScanner.class, GeofenceProvider.class,
        GeoFencesFetcherFactory.class, GoogleApiClientFactory.class, ScannerClient.class, SettingsActivity.class})
public class BeaconModule {
    private final Context appContext;

    public BeaconModule(Context appContext) {
        this.appContext = appContext;
    }

    @Provides
    public Context provideContext() {
        return appContext;
    }
}
