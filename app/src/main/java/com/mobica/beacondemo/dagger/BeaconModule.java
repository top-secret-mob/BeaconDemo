package com.mobica.beacondemo.dagger;

import android.content.Context;

import com.mobica.beacondemo.SplashScreen;
import com.mobica.beacondemo.DiscoveryManager;
import com.mobica.beacondemo.gcm.InstanceIdListenerService;
import com.mobica.beacondemo.gcm.WsGcmListenerService;
import com.mobica.beacondemo.settings.BluetoothPreferenceFragment;
import com.mobica.beacondemo.settings.SettingsActivity;
import com.mobica.discoverysdk.gcm.GcmMessageProxy;
import com.mobica.repositorysdk.RepositoryServiceAdapter;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger main module
 */
@Module(injects = {Context.class, GcmMessageProxy.class, InstanceIdListenerService.class,
        WsGcmListenerService.class, DiscoveryManager.class, SettingsActivity.class, BluetoothPreferenceFragment.class,
        SplashScreen.class, RepositoryServiceAdapter.class})
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
