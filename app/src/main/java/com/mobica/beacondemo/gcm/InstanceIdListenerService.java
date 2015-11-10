package com.mobica.beacondemo.gcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

public class InstanceIdListenerService extends InstanceIDListenerService {
    private static final String TAG = InstanceIdListenerService.class.getSimpleName();

    public InstanceIdListenerService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Log.d(TAG, "Gcm token refreshed");
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(GcmMessages.GCM_TOKEN_REFRESHED));
    }
}
