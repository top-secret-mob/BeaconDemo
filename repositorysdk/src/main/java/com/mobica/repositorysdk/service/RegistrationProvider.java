package com.mobica.repositorysdk.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.mobica.repositorysdk.ICredentials;
import com.mobica.repositorysdk.R;
import com.mobica.repositorysdk.model.RegisterRequest;
import com.mobica.repositorysdk.model.RegisterResponse;
import com.mobica.repositorysdk.model.WsResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

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
    @Inject
    RequestQueue requestQueue;
    private final ListeningExecutorService threadPool =
            MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());

    public ListenableFuture<RegisterResponse> login() {
        return Futures.transformAsync(getGcmToken(), new AsyncFunction<String, RegisterResponse>() {
            @Override
            public ListenableFuture<RegisterResponse> apply(String gcmToken) throws Exception {
                final String requestBody = new Gson().toJson(new RegisterRequest(credentials.getMacAddress(),
                        gcmToken));

                return makeRequest(requestQueue, Request.Method.POST,
                        context.getString(R.string.login_api), RegisterResponse.class, requestBody);
            }
        });
    }

    public ListenableFuture<WsResponse> logout() {
        return makeRequest(requestQueue, Request.Method.POST, context.getString(R.string.logout_api), WsResponse.class, null,
                getAuthHeader(credentials.getAuthToken()), null);
    }

    /**
     * Send Gcm token to our service
     */
    public ListenableFuture<WsResponse> subscribeForDiscoveryEvents() {
        return makeRequest(requestQueue, Request.Method.POST, context.getString(R.string.register_api), WsResponse.class,
                null, getAuthHeader(credentials.getAuthToken()), null);
    }

    /**
     * Send message to web service in order to unregister
     */
    public ListenableFuture<WsResponse> unsubscribeFromDiscoveryEvents() {
        return makeRequest(requestQueue, Request.Method.POST, context.getString(R.string.unregister_api), WsResponse.class,
                null, getAuthHeader(credentials.getAuthToken()), null);
    }

    // GCM token must be retrieved asynchronously
    private ListenableFuture<String> getGcmToken() {
        return threadPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return credentials.getGcmToken();
            }
        });
    }
}
