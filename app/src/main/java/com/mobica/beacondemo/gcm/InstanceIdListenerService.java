package com.mobica.beacondemo.gcm;

import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.mobica.beacondemo.BeaconApplication;
import com.mobica.repositorysdk.RepositoryServiceAdapter;

import javax.inject.Inject;

public class InstanceIdListenerService extends InstanceIDListenerService {
    private static final String TAG = InstanceIdListenerService.class.getSimpleName();

    @Inject
    RepositoryServiceAdapter repositoryServiceAdapter;

    public InstanceIdListenerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BeaconApplication.getGraph().inject(this);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Log.d(TAG, "Gcm token refreshed");
        TokenStore.updateToken(this);
        repositoryServiceAdapter.login();
    }
}