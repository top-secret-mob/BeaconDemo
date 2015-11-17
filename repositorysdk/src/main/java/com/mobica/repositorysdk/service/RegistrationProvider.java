package com.mobica.repositorysdk.service;

import android.content.Context;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.mobica.repositorysdk.ICredentials;
import com.mobica.repositorysdk.R;
import com.mobica.repositorysdk.model.DiscoveryRegisterRequest;
import com.mobica.repositorysdk.model.DiscoveryUnregisterRequest;
import com.mobica.repositorysdk.model.MacRegisterRequest;
import com.mobica.repositorysdk.model.MacUnregisterRequest;
import com.mobica.repositorysdk.model.WsResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by woos on 2015-11-13.
 */
@Singleton
public class RegistrationProvider extends AbstractProvider {
    @Inject
    Context context;
    @Inject
    ICredentials credentials;

    public void login() throws Exception {
        final String requestBody = new Gson().toJson(new MacRegisterRequest(getMacAddress(), credentials.getToken()));

        makeRequest(Request.Method.POST, context.getString(R.string.login_api), WsResponse.class, requestBody);
    }

    public void logout() throws Exception {
        final String requestBody = new Gson().toJson(new MacUnregisterRequest(getMacAddress()));

        makeRequest(Request.Method.POST, context.getString(R.string.logout_api), WsResponse.class, requestBody);
    }

    /**
     * Send Gcm token to our service
     */
    public void registerForDiscoveryEvents() throws Exception {
        final String requestBody = new Gson().toJson(new DiscoveryRegisterRequest(getMacAddress()));

        makeRequest(Request.Method.POST, context.getString(R.string.register_api), WsResponse.class, requestBody);
    }

    /**
     * Send message to web service in order to unregister
     */
    public void unregisterFromDiscoveryEvents() throws Exception {
        final String requestBody = new Gson().toJson(new DiscoveryUnregisterRequest(getMacAddress()));

        makeRequest(Request.Method.POST, context.getString(R.string.unregister_api), WsResponse.class, requestBody);
    }
}
