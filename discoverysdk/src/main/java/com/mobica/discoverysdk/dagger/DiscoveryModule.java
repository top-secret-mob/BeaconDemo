package com.mobica.discoverysdk.dagger;

import android.content.Context;

import com.mobica.discoverysdk.geofence.GeofenceProvider;
import com.mobica.discoverysdk.geofence.GoogleApiClientFactory;
import com.mobica.discoverysdk.location.ILocationProvider;
import com.mobica.discoverysdk.nfc.DiscoverActivity;
import com.mobica.discoverysdk.wifi.ScannerClient;
import com.mobica.discoverysdk.wifi.WifiScanner;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger main module
 */
@Module(injects = {Context.class, WifiScanner.class, GeofenceProvider.class, DiscoverActivity.class,
        ScannerClient.class, GoogleApiClientFactory.class, ILocationProvider.class, Context.class},
        library = true, complete = false)
public class DiscoveryModule {
    private final ILocationProvider locationProvider;

    public DiscoveryModule(ILocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }

    @Provides
    public ILocationProvider provideLocationProvider() {
        return locationProvider;
    }
}
