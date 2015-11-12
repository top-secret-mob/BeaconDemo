package com.mobica.discoverysdk.dagger;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.mobica.discoverysdk.gcm.DiscoveryRegistrationIntentService;
import com.mobica.discoverysdk.geofence.GeoFencesFetcherFactory;
import com.mobica.discoverysdk.geofence.GeofenceProvider;
import com.mobica.discoverysdk.geofence.GoogleApiClientFactory;
import com.mobica.discoverysdk.nfc.DiscoverActivity;
import com.mobica.discoverysdk.wifi.ScannerClient;
import com.mobica.discoverysdk.wifi.WifiScanner;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger main module
 */
@Module(injects = {Context.class, WifiScanner.class, GeofenceProvider.class, DiscoverActivity.class,
        GeoFencesFetcherFactory.class, GoogleApiClientFactory.class, RequestQueue.class,
        DiscoveryRegistrationIntentService.class})
public class DiscoveryModule {
    private final Context appContext;
    private final RequestQueue requestQueue;

    public DiscoveryModule(Context appContext, RequestQueue queue) {
        this.appContext = appContext;
        this.requestQueue = queue;
    }

    @Provides
    public Context provideContext() {
        return appContext;
    }

    @Provides
    public RequestQueue provideRequestQueue() {
        return requestQueue;
    }
}
