package com.mobica.beacondemo.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class WsGcmListenerService extends GcmListenerService {
    private static final String TAG = WsGcmListenerService.class.getSimpleName();

    public WsGcmListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Log.d(TAG, "RECEIVED: from:" + from + " data:" + data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(GcmPreferences.WS_MESSAGE));
    }
}
