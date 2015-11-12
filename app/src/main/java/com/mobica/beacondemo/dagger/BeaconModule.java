package com.mobica.beacondemo.dagger;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.mobica.beacondemo.SettingsActivity;
import com.mobica.beacondemo.ble.DiscoveryManager;
import com.mobica.beacondemo.gcm.RegistrationIntentService;
import com.mobica.beacondemo.gcm.WsGcmListenerService;
import com.mobica.discoverysdk.gcm.GcmMessageProxy;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger main module
 */
@Module(injects = {Context.class, GcmMessageProxy.class, RequestQueue.class, RegistrationIntentService.class,
        WsGcmListenerService.class, DiscoveryManager.class, SettingsActivity.class})
public class BeaconModule {
    private final Context appContext;
    private final RequestQueue requestQueue;

    public BeaconModule(Context appContext, RequestQueue queue) {
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
