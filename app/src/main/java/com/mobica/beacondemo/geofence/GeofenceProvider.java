package com.mobica.beacondemo.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provider class for geo fencing functionality
 */
public class GeofenceProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GeoFencesFetcher.GeoFencesFetcherListener {
    private static final String TAG = GeofenceProvider.class.getSimpleName();
    private final Context context;
    // Stores the PendingIntent used to request geo fence monitoring.
    private PendingIntent geofenceRequestIntent;
    private GoogleApiClient apiClient;
    private GeoFencesFetcher fetcher;
    private List<Geofence> geofenceList;
    private final AtomicBoolean connected = new AtomicBoolean();

    public GeofenceProvider(Context context) {
        this.context = context;
    }

    public synchronized void connect() {
        fetcher = new GeoFencesFetcher(this);
        apiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        apiClient.connect();
    }

    public synchronized void disconnect() {
        if (apiClient != null) {
            apiClient.disconnect();
            apiClient = null;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "LocationServices connected");

        connected.set(true);
        fetcher.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "LocationServices connection suspended");

        connected.set(false);
        fetcher.cancel(true);

        if (geofenceRequestIntent != null && geofenceList != null) {
            LocationServices.GeofencingApi.removeGeofences(apiClient, geofenceRequestIntent);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "LocationServices connection failed");
        connected.set(false);
    }

    @Override
    public void onGeoFencesFetched(List<Geofence> geofences) {
        if (!connected.get()) {
            return;
        }

        Log.d(TAG, "Retrieved " + geofences.size() + " geo fences");

        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        geofenceRequestIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.geofenceList = geofences;
        LocationServices.GeofencingApi.addGeofences(apiClient, geofenceList, geofenceRequestIntent);
    }

    @Override
    public void onGeoFencesFetchingFailed(Throwable error) {
        Log.e(TAG, "Failed to retrieve geo fences");
    }
}
