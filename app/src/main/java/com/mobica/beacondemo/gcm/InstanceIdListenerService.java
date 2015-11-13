package com.mobica.beacondemo.gcm;

import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.mobica.beacondemo.registration.RegistrationProvider;

import javax.inject.Inject;

public class InstanceIdListenerService extends InstanceIDListenerService implements
        RegistrationProvider.RegistrationProviderListener {
    private static final String TAG = InstanceIdListenerService.class.getSimpleName();

    @Inject
    RegistrationProvider registrationProvider;

    public InstanceIdListenerService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Log.d(TAG, "Gcm token refreshed");
        TokenStore.updateToken(this);
        registrationProvider.login(this);
    }

    @Override
    public void onOperationSucceeded() {
        Log.d(TAG, "Token refresh succeeded");
    }

    @Override
    public void onOperationFailed(String error) {
        Log.d(TAG, "Token refresh failed: " + error);
    }
}