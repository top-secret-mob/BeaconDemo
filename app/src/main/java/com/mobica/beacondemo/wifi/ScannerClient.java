package com.mobica.beacondemo.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mobica.beacondemo.gcm.GcmMessages;
import com.mobica.beacondemo.gcm.RegistrationIntentService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * WIFI passive scanner client
 */
@Singleton
public class ScannerClient {
    private static final String TAG = ScannerClient.class.getSimpleName();

    public enum State {
        UNREGISTERED,
        REGISTERING,
        REGISTERED,
        DEREGISTERING
    }

    private State state = State.UNREGISTERED;

    @Inject
    Context context;

    public synchronized void register() {
        if (state == State.UNREGISTERED) {
            state = State.REGISTERING;

            LocalBroadcastManager.getInstance(context).registerReceiver(localReceiver,
                    GcmMessages.getIntentFilter());
            final Intent intent = new Intent(context, RegistrationIntentService.class);
            intent.setAction(RegistrationIntentService.ACTION_REGISTER);
            context.startService(intent);
        }
    }

    public synchronized void unregister() {
        if (state == State.REGISTERED) {
            state = State.DEREGISTERING;

            final Intent intent = new Intent(context, RegistrationIntentService.class);
            intent.setAction(RegistrationIntentService.ACTION_REGISTER);
            context.startService(intent);
        }
    }

    public synchronized State getState() {
        return state;
    }

    private synchronized void setState(State state) {
        this.state = state;
    }

    private synchronized void refreshToken() {
        if (state == State.REGISTERED) {
            final Intent intent = new Intent(context, RegistrationIntentService.class);
            intent.setAction(RegistrationIntentService.ACTION_REGISTER);
            context.startService(intent);
        }
    }

    private final BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (GcmMessages.GCM_TOKEN_REFRESHED.equals(action)) {
                Log.d(TAG, "GCM token refreshed");
                refreshToken();
            } else if (GcmMessages.WS_REGISTRATION_SUCCEEDED.equals(action)) {
                Log.d(TAG, "Scanner client registered");
                setState(State.REGISTERED);
            } else if (GcmMessages.WS_REGISTRATION_FAILED.equals(action)) {
                Log.d(TAG, "Scanner client registration failed");
                setState(State.UNREGISTERED);
            } else if (GcmMessages.WS_DEREGISTRATION_SUCCEEDED.equals(action)) {
                Log.d(TAG, "Scanner client unregistered");
                setState(State.UNREGISTERED);
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
            } else if (GcmMessages.WS_DEREGISTRATION_FAILED.equals(action)) {
                Log.d(TAG, "Scanner client deregistration failed");
                setState(State.REGISTERED);
            }
        }
    };
}
