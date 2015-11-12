package com.mobica.beacondemo.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.mobica.beacondemo.BeaconApplication;
import com.mobica.discoverysdk.gcm.GcmMessageProxy;

import javax.inject.Inject;

public class WsGcmListenerService extends GcmListenerService {
    private static final String TAG = WsGcmListenerService.class.getSimpleName();

    @Inject
    GcmMessageProxy gcmMessageProxy;

    @Override
    public void onCreate() {
        super.onCreate();
        BeaconApplication.getGraph().inject(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Log.d(TAG, "RECEIVED GCM: from:" + from + " data:" + data);

        if (!gcmMessageProxy.onMessageReceived(from, data)) {
            Log.d(TAG, "Message not recognized");
        }
    }
}
