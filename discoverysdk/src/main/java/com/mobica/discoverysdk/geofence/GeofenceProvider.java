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
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mobica.discoverysdk.dagger.Graphs;
import com.mobica.discoverysdk.location.ILocationProvider;
import com.mobica.repositorysdk.RepositoryAdapter;
import com.mobica.repositorysdk.model.GeoFence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Provider class for geo fencing functionality
 */
public class GeofenceProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
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
    private List<Geofence> geofenceList;
    private State state = State.DISCONNECTED;
    private ListenableFuture<List<GeoFence>> geofenceFuture;

    @Inject
    Context context;
    @Inject
    GoogleApiClientFactory googleApiClientFactory;
    @Inject
    RepositoryAdapter repositoryService;
    @Inject
    ILocationProvider locationProvider;

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
        fetchGeofences();

        final IntentFilter filter = new IntentFilter(GeofenceTransitionsIntentService.GEOFENCE_ENTERED);
        filter.addAction(GeofenceTransitionsIntentService.GEOFENCE_EXIT);
        LocalBroadcastManager.getInstance(context).registerReceiver(localReceiver, filter);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "LocationServices connection suspended");

        state = State.SUSPENDED;
        cancelGeofencesFetching();

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

    private void fetchGeofences() {
        geofenceFuture = repositoryService.getGeoFences(locationProvider.getCurrentLocation());
        Futures.addCallback(geofenceFuture, new FutureCallback<List<GeoFence>>() {
            @Override
            public void onSuccess(final List<GeoFence> geofences) {
                if (getState() != State.CONNECTED) {
                    return;
                }

                Log.d(TAG, "Retrieved " + geofences.size() + " geo fences");

                Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
                geofenceRequestIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                geofenceList = new ArrayList<>();
                for (GeoFence geoFence : geofences) {
                    geofenceList.add(new Geofence.Builder()
                            .setRequestId(geoFence.getId())
                            .setCircularRegion(geoFence.getLatitude(), geoFence.getLongitude(), geoFence.getRadius())
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());
                }

                final GeofencingRequest request = new GeofencingRequest.Builder()
                        .addGeofences(geofenceList)
                        .build();
                LocationServices.GeofencingApi.addGeofences(apiClient, request, geofenceRequestIntent);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Failed to retrieve geo fences, reason:" + t.getMessage());
            }
        });
    }

    private void cancelGeofencesFetching() {
        if (geofenceFuture != null) {
            geofenceFuture.cancel(true);
        }
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
