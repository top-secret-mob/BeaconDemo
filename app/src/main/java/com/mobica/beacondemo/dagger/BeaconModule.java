package com.mobica.beacondemo.dagger;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mobica.beacondemo.SplashScreen;
import com.mobica.beacondemo.DiscoveryManager;
import com.mobica.beacondemo.gcm.InstanceIdListenerService;
import com.mobica.beacondemo.gcm.WsGcmListenerService;
import com.mobica.beacondemo.settings.BluetoothPreferenceFragment;
import com.mobica.beacondemo.settings.SettingsActivity;
import com.mobica.beacondemo.utils.Credentials;
import com.mobica.discoverysdk.gcm.GcmMessageProxy;
import com.mobica.repositorysdk.ICredentials;
import com.mobica.repositorysdk.RepositoryAdapter;
import com.mobica.repositorysdk.volley.CertsManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger main module
 */
@Module(injects = {Context.class, GcmMessageProxy.class, InstanceIdListenerService.class,
        WsGcmListenerService.class, DiscoveryManager.class, SettingsActivity.class, BluetoothPreferenceFragment.class,
        SplashScreen.class, RepositoryAdapter.class, ICredentials.class, RequestQueue.class, CertsManager.class})
public class BeaconModule {
    private final Context appContext;
    private final RequestQueue requestQueue;

    public BeaconModule(Context appContext) {
        this.appContext = appContext;
        this.requestQueue = Volley.newRequestQueue(appContext);
    }

    @Provides
    public RequestQueue provideRequestQueue() {
        return requestQueue;
    }

    @Provides
    public Context provideContext() {
        return appContext;
    }

    @Provides
    @Singleton
    public ICredentials provideCredentials() {
        return new Credentials();
    }

    @Provides
    @Singleton
    public GcmMessageProxy provideGcmMessageProxy() {
        return new GcmMessageProxy();
    }

    @Provides
    @Singleton
    public CertsManager provideCertsManager() {
        return new CertsManager();
    }
}
