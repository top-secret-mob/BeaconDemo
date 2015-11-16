package com.mobica.beacondemo.dagger;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.mobica.beacondemo.SplashScreen;
import com.mobica.beacondemo.ble.DiscoveryManager;
import com.mobica.beacondemo.gcm.InstanceIdListenerService;
import com.mobica.beacondemo.gcm.WsGcmListenerService;
import com.mobica.beacondemo.registration.RegistrationProvider;
import com.mobica.beacondemo.settings.BluetoothPreferenceFragment;
import com.mobica.beacondemo.settings.SettingsActivity;
import com.mobica.discoverysdk.gcm.GcmMessageProxy;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger main module
 */
@Module(injects = {Context.class, GcmMessageProxy.class, RequestQueue.class, InstanceIdListenerService.class,
        WsGcmListenerService.class, DiscoveryManager.class, SettingsActivity.class, BluetoothPreferenceFragment.class,
        RegistrationProvider.class, SplashScreen.class})
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
