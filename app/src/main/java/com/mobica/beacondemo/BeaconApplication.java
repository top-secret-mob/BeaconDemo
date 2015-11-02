package com.mobica.beacondemo;

import android.app.Application;

import com.mobica.beacondemo.volley.VolleyScheduler;

/**
 * Created by woos on 2015-11-02.
 */
public class BeaconApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize volley
        VolleyScheduler.init(this);
    }
}
