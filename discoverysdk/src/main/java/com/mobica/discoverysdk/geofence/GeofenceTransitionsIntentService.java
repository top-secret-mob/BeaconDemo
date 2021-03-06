package com.mobica.discoverysdk.geofence;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Listens for geofence transition changes.
 */
public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();
    public static final String GEOFENCE_ENTERED = "GEOFENCE_ENTERED";
    public static final String GEOFENCE_EXIT = "GEOFENCE_EXIT";

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    /**
     * Handles incoming intents.
     *
     * @param intent The Intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
            Log.e(TAG, "Location Services error: " + errorCode);
        } else {
            int transitionType = geoFenceEvent.getGeofenceTransition();
            List<Geofence> triggeredFences = geoFenceEvent.getTriggeringGeofences();

            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                Log.d(TAG, "== ENTERING " + triggeredFences.get(0).getRequestId());
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(GEOFENCE_ENTERED));
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                Log.d(TAG, "== EXITING " + triggeredFences.get(0).getRequestId());
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(GEOFENCE_EXIT));
            }
        }
    }
}
