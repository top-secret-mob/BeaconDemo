package com.mobica.discoverysdk.gcm;

import android.content.IntentFilter;

/**
 * Created by woos on 2015-11-10.
 */
public class GcmDiscoveryMessages {
    public static final String WS_REGISTRATION_SUCCEEDED = "WS_REGISTRATION_SUCCEEDED";
    public static final String WS_REGISTRATION_FAILED = "WS_REGISTRATION_FAILED";
    public static final String WS_DEREGISTRATION_SUCCEEDED = "WS_DEREGISTRATION_SUCCEEDED";
    public static final String WS_DEREGISTRATION_FAILED = "WS_DEREGISTRATION_FAILED";

    public static IntentFilter getIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(WS_REGISTRATION_SUCCEEDED);
        filter.addAction(WS_REGISTRATION_FAILED);
        filter.addAction(WS_DEREGISTRATION_SUCCEEDED);
        filter.addAction(WS_DEREGISTRATION_FAILED);
        return filter;
    }
}
