package com.mobica.beacondemo.utils;

import com.mobica.beacondemo.BeaconApplication;
import com.mobica.beacondemo.gcm.TokenStore;
import com.mobica.repositorysdk.ICredentials;

/**
 * Created by woos on 2015-11-17.
 */
public class Credentials implements ICredentials {
    @Override
    public String getMacAddress() {
        return HwUtils.getWifiMacAddress();
    }

    @Override
    public String getToken() {
        // This can't be called on main thread
        return TokenStore.getToken(BeaconApplication.getAppContext());
    }
}
