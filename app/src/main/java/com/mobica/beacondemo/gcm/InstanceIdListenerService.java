package com.mobica.beacondemo.gcm;

import android.content.Intent;
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
        startService(new Intent(this, RegistrationIntentService.class));
    }
}
