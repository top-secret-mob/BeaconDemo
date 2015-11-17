package com.mobica.repositorysdk.service;

import android.content.Context;
import android.location.Location;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.mobica.repositorysdk.R;
import com.mobica.repositorysdk.model.GeoFencesRequest;
import com.mobica.repositorysdk.model.GeoFencesResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by woos on 2015-11-17.
 */
@Singleton
public class GeofenceProvider extends AbstractProvider {
    @Inject
    Context context;

    public GeoFencesResponse getGeoFences(Location location) throws Exception {
        final String requestBody;
        if (location != null) {
            requestBody = new Gson().toJson(new GeoFencesRequest(getMacAddress(),
                    location.getLatitude(), location.getLongitude()));
        } else {
            requestBody = new Gson().toJson(new GeoFencesRequest(getMacAddress(), 0, 0));
        }

        return makeRequest(Request.Method.POST, context.getString(R.string.geofence_api),
                GeoFencesResponse.class, requestBody);
    }
}
