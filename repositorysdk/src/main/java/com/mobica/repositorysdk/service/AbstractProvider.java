package com.mobica.repositorysdk.service;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.SettableFuture;
import com.mobica.repositorysdk.ICredentials;
import com.mobica.repositorysdk.model.WsResponse;
import com.mobica.repositorysdk.volley.GsonRequest;

import javax.inject.Inject;

/**
 * Created by woos on 2015-11-18.
 */
public abstract class AbstractProvider {
    @Inject
    RequestQueue requestQueue;
    @Inject
    ICredentials credentials;

    protected <T extends WsResponse> T makeRequest(int method, String api, Class<T> responseClass,
                                                   String requestBody) throws Exception {
        final SettableFuture<T> result = SettableFuture.create();

        GsonRequest<T> req = new GsonRequest<>(method, api, responseClass, requestBody,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        Log.d(getClass().getSimpleName(), "WS logout finished=" + response.getStatus());

                        if (response.getStatus() == WsResponse.Status.success) {
                            result.set(response);
                        } else {
                            result.setException(new RuntimeException(response.getError()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getSimpleName(), "WS logout failed=" + error.getCause());
                result.setException(new RuntimeException(error.getMessage()));
            }
        });

        req.setTag(getClass().getSimpleName());
        requestQueue.add(req);
        return result.get();
    }

    protected String getMacAddress() {
        final String mac = credentials.getMacAddress();
        if (Strings.isNullOrEmpty(mac)) {
            Log.e(getClass().getSimpleName(), "Failed to retrieve Wifi adapter MAC address");
            throw new RuntimeException("Wifi adapter MAC address retrieving failed");
        }
        return mac;
    }
}
