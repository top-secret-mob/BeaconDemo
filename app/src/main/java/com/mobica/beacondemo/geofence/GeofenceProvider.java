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

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provider class for geo fencing functionality
 */
@Singleton
public class GeofenceProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GeoFencesFetcher.GeoFencesFetcherListener {
    private static final String TAG = GeofenceProvider.class.getSimpleName();

    public enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        SUSPENDED
    }

    // Stores the PendingIntent used to request geo fence monitoring.
    private PendingIntent geofenceRequestIntent;
    private GoogleApiClient apiClient;
    private GeoFencesFetcher fetcher;
    private List<Geofence> geofenceList;
    private State state = State.DISCONNECTED;

    @Inject
    Context context;
    @Inject
    GeoFencesFetcherFactory geoFencesFetcherFactory;
    @Inject
    GoogleApiClientFactory googleApiClientFactory;

    public synchronized void connect() {
        if (state != State.DISCONNECTED) {
            // already connected/connecting
            return;
        }

        apiClient = googleApiClientFactory.create(context, LocationServices.API, this, this);

        state = State.CONNECTING;
        apiClient.connect();
    }

    public synchronized void disconnect() {
        if (state != State.DISCONNECTED) {
            apiClient.disconnect();
            state = State.DISCONNECTED;
        }
    }

    public synchronized State getState() {
        return state;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "LocationServices connected");

        state = State.CONNECTED;
        fetcher = geoFencesFetcherFactory.create(this);
        fetcher.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "LocationServices connection suspended");

        state = State.SUSPENDED;
        fetcher.cancel(true);

        if (geofenceRequestIntent != null && geofenceList != null) {
            LocationServices.GeofencingApi.removeGeofences(apiClient, geofenceRequestIntent);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "LocationServices connection failed");
        state = State.DISCONNECTED;
    }

    @Override
    public void onGeoFencesFetched(List<Geofence> geofences) {
        if (getState() != State.CONNECTED) {
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
