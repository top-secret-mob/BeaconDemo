package com.mobica.beacondemo;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mobica.beacondemo.ble.BleAdapter;
import com.mobica.beacondemo.ble.DiscoveryManager;
import com.mobica.beacondemo.config.ConfigStorage;
import com.mobica.beacondemo.dagger.BeaconModule;
import com.mobica.beacondemo.repository.RepositoryServiceAdapter;
import com.mobica.discoverysdk.DiscoverySdk;

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
        BeaconApplication.graph = ObjectGraph.create(new BeaconModule(this, Volley.newRequestQueue(this)));

        // initialize discovery sdk
        DiscoverySdk.init(this, graph.get(RequestQueue.class));


        ConfigStorage.setup();
        ConfigStorage.wasBleEnabled.set(BleAdapter.isBleEnabled(this));

        graph.get(RepositoryServiceAdapter.class).connect(this);

        final DiscoveryManager discoveryManager = graph.get(DiscoveryManager.class);
        discoveryManager.updateModes(ConfigStorage.bleSwitchModes.get());
    }

    public static Context getAppContext() {
        return applicationContext;
    }

    public static ObjectGraph getGraph() {
        return graph;
    }
}
