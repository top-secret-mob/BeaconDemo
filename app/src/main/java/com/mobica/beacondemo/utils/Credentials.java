package com.mobica.beacondemo.utils;

import com.mobica.beacondemo.BeaconApplication;
import com.mobica.beacondemo.gcm.TokenStore;
import com.mobica.repositorysdk.ICredentials;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by woos on 2015-11-17.
 */
public class Credentials implements ICredentials {
    private final AtomicReference<String> authToken = new AtomicReference<>();

    @Override
    public String getMacAddress() {
        return HwUtils.getWifiMacAddress();
    }

    @Override
    public String getAuthToken() {
        return authToken.get();
    }

    @Override
    public void setAuthToken(String token) {
        authToken.set(token);
    }

    @Override
    public String getGcmToken() {
        // This can't be called on main thread
        return TokenStore.getToken(BeaconApplication.getAppContext());
    }
}
