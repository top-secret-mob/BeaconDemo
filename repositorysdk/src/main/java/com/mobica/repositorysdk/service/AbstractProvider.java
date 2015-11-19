package com.mobica.repositorysdk.service;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.mobica.repositorysdk.model.WsResponse;
import com.mobica.repositorysdk.volley.GsonRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by woos on 2015-11-18.
 */
public abstract class AbstractProvider {

    protected <T extends WsResponse> ListenableFuture<T> makeRequest(RequestQueue requestQueue,
                                                                     int method, String api, Class<T> responseClass,
                                                                     String requestBody) {
        return makeRequest(requestQueue, method, api, responseClass, requestBody, null, null);
    }

    protected <T extends WsResponse> ListenableFuture<T> makeRequest(RequestQueue requestQueue, int method, String api,
                                                                     Class<T> responseClass, String requestBody, Map<String, String> headers,
                                                                     Map<String, String> params) {
        final SettableFuture<T> result = SettableFuture.create();

        Log.d(getClass().getSimpleName(), "New request: " + api);

        GsonRequest<T> req = new GsonRequest<>(method, api, responseClass, requestBody, headers, params,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        Log.d(getClass().getSimpleName(), "Request finished=" + response.getStatus());

                        if (response.getStatus() == WsResponse.Status.success) {
                            result.set(response);
                        } else {
                            result.setException(new RuntimeException(response.getError()));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getSimpleName(), "Request failed=" + error.getCause());
                result.setException(new RuntimeException(error.getMessage()));
            }
        });

        req.setTag(getClass().getSimpleName());
        requestQueue.add(req);
        return result;
    }

    protected static HashMap<String, String> getAuthHeader(String authToken) {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "key=" + authToken);
        return headers;
    }
}
