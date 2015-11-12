package com.mobica.discoverysdk.geofence;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.mobica.discoverysdk.dagger.Graphs;

import java.util.List;

import javax.inject.Inject;

/**
 * Provider class for geo fencing functionality
 */
public class GeofenceProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GeoFencesFetcher.GeoFencesFetcherListener {
    private static final String TAG = GeofenceProvider.class.getSimpleName();

    public enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        SUSPENDED
    }

    public interface GeofenceProviderListener {
        void onGeofenceApiConnected();

        void onGeofenceApiConnectionFailed(String error);

        void onGeofenceEntered();

        void onGeofenceExit();
    }

    private final GeofenceProviderListener listener;
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

    public GeofenceProvider(GeofenceProviderListener listener) {
        this.listener = listener;

        Graphs.inject(this);
    }

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

            LocalBroadcastManager.getInstance(context).unregisterReceiver(localReceiver);
        }
    }

    public synchronized State getState() {
        return state;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "LocationServices connected");

        listener.onGeofenceApiConnected();

        state = State.CONNECTED;
        fetcher = geoFencesFetcherFactory.create(this);
        fetcher.execute();

        final IntentFilter filter = new IntentFilter(GeofenceTransitionsIntentService.GEOFENCE_ENTERED);
        filter.addAction(GeofenceTransitionsIntentService.GEOFENCE_EXIT);
        LocalBroadcastManager.getInstance(context).registerReceiver(localReceiver, filter);
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

        listener.onGeofenceApiConnectionFailed(connectionResult.getErrorMessage());
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
        final GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofences(geofences)
                .build();
        LocationServices.GeofencingApi.addGeofences(apiClient, request, geofenceRequestIntent);
    }

    @Override
    public void onGeoFencesFetchingFailed(Throwable error) {
        Log.e(TAG, "Failed to retrieve geo fences");
    }

    private final BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (GeofenceTransitionsIntentService.GEOFENCE_ENTERED.equals(action)) {
                listener.onGeofenceEntered();
            } else if (GeofenceTransitionsIntentService.GEOFENCE_EXIT.equals(action)) {
                listener.onGeofenceExit();
            }
        }
    };
}
