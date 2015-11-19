package com.mobica.repositorysdk.service;

import android.content.Context;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.common.util.concurrent.ListenableFuture;
import com.mobica.repositorysdk.ICredentials;
import com.mobica.repositorysdk.R;
import com.mobica.repositorysdk.model.GeoFencesResponse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by woos on 2015-11-17.
 */
@Singleton
public class GeofenceProvider extends AbstractProvider {
    @Inject
    Context context;
    @Inject
    ICredentials credentials;
    @Inject
    RequestQueue requestQueue;

    public ListenableFuture<GeoFencesResponse> getGeoFences(Location location) {
        final Map<String, String> reqParams = new HashMap<>();
        if (location != null) {
            reqParams.put("latitude", String.valueOf(location.getLatitude()));
            reqParams.put("longitude", String.valueOf(location.getLongitude()));
        } else {
            reqParams.put("latitude", "0");
            reqParams.put("longitude", "0");
        }

        return makeRequest(requestQueue, Request.Method.GET, context.getString(R.string.geofence_api),
                GeoFencesResponse.class, null, getAuthHeader(credentials.getAuthToken()), reqParams);
    }
}
