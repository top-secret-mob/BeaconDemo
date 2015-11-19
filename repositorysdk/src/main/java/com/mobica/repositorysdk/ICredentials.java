package com.mobica.repositorysdk;

/**
 * Created by woos on 2015-11-17.
 */
public interface ICredentials {

    String getMacAddress();

    String getAuthToken();

    void setAuthToken(String token);

    String getGcmToken();
}
