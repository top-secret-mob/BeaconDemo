package com.mobica.beacondemo.repository;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.mobica.beacondemo.R;
import com.mobica.beacondemo.gcm.TokenStore;
import com.mobica.beacondemo.model.MacRegisterRequest;
import com.mobica.beacondemo.model.MacUnregisterRequest;
import com.mobica.discoverysdk.gcm.model.WsResponse;
import com.mobica.discoverysdk.utils.HwUtils;
import com.mobica.discoverysdk.volley.GsonRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by woos on 2015-11-13.
 */
@Singleton
public class RegistrationProvider {
    private static final String TAG = RegistrationProvider.class.getSimpleName();

    @Inject
    Context context;
    @Inject
    RequestQueue requestQueue;

    public void login() throws Exception {
        final String mac = HwUtils.getWifiMacAddress();
        if (Strings.isNullOrEmpty(mac)) {
            Log.e(TAG, "Failed to retrieve Wifi adapter MAC address");
            throw new RuntimeException("Wifi adapter MAC address retrieving failed");
        }

        final SettableFuture<Void> result = SettableFuture.create();
        final String requestBody = new Gson().toJson(new MacRegisterRequest(mac, TokenStore.getToken(context)));

        GsonRequest<WsResponse> req = new GsonRequest<>(Request.Method.POST,
                context.getString(R.string.login_api), WsResponse.class, requestBody,
                new Response.Listener<WsResponse>() {
                    @Override
                    public void onResponse(WsResponse response) {
                        Log.d(TAG, "WS login finished=" + response.getStatus());

                        if (response.getStatus() == WsResponse.Status.success) {
                            result.set(null);
                        } else {
                            result.setException(new RuntimeException(response.getError()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "WS login failed=" + error.getCause());
                result.setException(new RuntimeException(error.getMessage()));
            }
        });

        req.setTag(TAG);
        requestQueue.add(req);
        result.get();
    }

    public void logout() throws Exception {
        final String mac = HwUtils.getWifiMacAddress();
        if (Strings.isNullOrEmpty(mac)) {
            Log.e(TAG, "Failed to retrieve Wifi adapter MAC address");
            throw new RuntimeException("Wifi adapter MAC address retrieving failed");
        }

        final SettableFuture<Void> result = SettableFuture.create();
        final String requestBody = new Gson().toJson(new MacUnregisterRequest(mac));

        GsonRequest<WsResponse> req = new GsonRequest<>(Request.Method.POST,
                context.getString(R.string.logout_api), WsResponse.class, requestBody,
                new Response.Listener<WsResponse>() {
                    @Override
                    public void onResponse(WsResponse response) {
                        Log.d(TAG, "WS logout finished=" + response.getStatus());

                        if (response.getStatus() == WsResponse.Status.success) {
                            result.set(null);
                        } else {
                            result.setException(new RuntimeException(response.getError()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "WS logout failed=" + error.getCause());
                result.setException(new RuntimeException(error.getMessage()));
            }
        });

        req.setTag(TAG);
        requestQueue.add(req);
        result.get();
    }

    public void cancel() {
        requestQueue.cancelAll(TAG);
    }
}