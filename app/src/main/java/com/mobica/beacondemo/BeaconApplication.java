package com.mobica.beacondemo;

import android.app.Application;
import android.content.Context;

import com.mobica.beacondemo.ble.BleAdapter;
import com.mobica.beacondemo.ble.DiscoveryManager;
import com.mobica.beacondemo.config.ConfigStorage;
import com.mobica.beacondemo.dagger.BeaconModule;
import com.mobica.beacondemo.volley.VolleyScheduler;

import dagger.ObjectGraph;

/**
 * Created by woos on 2015-11-02.
 */
public class BeaconApplication extends Application {
    private static Context applicationContext;
    private static ObjectGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();
        BeaconApplication.applicationContext = this;

        // initialize dagger
        BeaconApplication.graph = ObjectGraph.create(new BeaconModule(this));

        // initialize volley
        VolleyScheduler.init(this);

        ConfigStorage.setup();
        ConfigStorage.wasBleEnabled.set(BleAdapter.isBleEnabled(this));

        final DiscoveryManager discoveryManager = graph.get(DiscoveryManager.class);
        discoveryManager.updateModes(ConfigStorage.bleSwitchMode.get());
    }

    public static Context getAppContext() {
        return applicationContext;
    }

    public static ObjectGraph getGraph() {
        return graph;
    }
}
