package com.mobica.beacondemo;

import android.app.Application;
import android.content.Context;

import com.mobica.beacondemo.ble.BleAdapter;
import com.mobica.beacondemo.config.ConfigStorage;
import com.mobica.beacondemo.dagger.BeaconModule;
import com.mobica.beacondemo.location.LocationProvider;
import com.mobica.discoverysdk.dagger.DiscoveryModule;
import com.mobica.repositorysdk.RepositoryAdapter;
import com.mobica.repositorysdk.dagger.RepositoryModule;

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
        BeaconApplication.graph = ObjectGraph.create(new BeaconModule(this),
                new DiscoveryModule(new LocationProvider()), new RepositoryModule());

        // initialize repository sdk
        com.mobica.repositorysdk.dagger.Graphs.init(graph);
        // initialize discovery sdk
        com.mobica.discoverysdk.dagger.Graphs.init(graph);


        ConfigStorage.setup();
        ConfigStorage.wasBleEnabled.set(BleAdapter.isBleEnabled(this));

        graph.get(RepositoryAdapter.class).login();

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
