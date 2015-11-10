package com.mobica.beacondemo.geofence;

import android.content.Context;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;

/**
 * {@link com.google.android.gms.common.api.GoogleApiClient} objects factory
 */
public class GoogleApiClientFactory {

    @Inject
    public GoogleApiClientFactory() {
    }

    public GoogleApiClient create(Context context, Api<? extends Api.ApiOptions.NotRequiredOptions> api,
                                  GoogleApiClient.ConnectionCallbacks listener,
                                  GoogleApiClient.OnConnectionFailedListener errorListener) {
        return new GoogleApiClient.Builder(context)
                .addApi(api)
                .addConnectionCallbacks(listener)
                .addOnConnectionFailedListener(errorListener)
                .build();
    }
}
