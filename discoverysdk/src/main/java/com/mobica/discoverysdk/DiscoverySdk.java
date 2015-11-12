package com.mobica.discoverysdk;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.mobica.discoverysdk.dagger.DiscoveryModule;
import com.mobica.discoverysdk.dagger.Graphs;

/**
 * Entry point for discovery sdk
 */
public class DiscoverySdk {

    public static void init(Context context, RequestQueue requestQueue) {
        Graphs.init(new DiscoveryModule(context, requestQueue));
    }
}
