package com.mobica.beacondemo;

import android.app.Application;
import android.content.Context;

import com.mobica.beacondemo.ble.BleAdapter;
import com.mobica.beacondemo.config.ConfigStorage;
import com.mobica.beacondemo.volley.VolleyScheduler;

/**
 * Created by woos on 2015-11-02.
 */
public class BeaconApplication extends Application {
    private static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        BeaconApplication.applicationContext = this;

        // initialize volley
        VolleyScheduler.init(this);

        ConfigStorage.setup();
        ConfigStorage.wasBleEnabled.set(BleAdapter.isBleEnabled(this));
    }

    public static Context getAppContext() {
        return applicationContext;
    }
}
