package com.mobica.discoverysdk.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mobica.discoverysdk.gcm.DiscoveryRegistrationIntentService;
import com.mobica.discoverysdk.gcm.GcmDiscoveryMessages;
import com.mobica.discoverysdk.gcm.GcmMessageListener;
import com.mobica.discoverysdk.gcm.GcmMessageProxy;

import javax.inject.Singleton;

/**
 * WIFI passive scanner client
 */
@Singleton
public class ScannerClient {
    private static final String TAG = ScannerClient.class.getSimpleName();
    // GCM message identifier
    private static final String PARAM_MESSAGE_TYPE = "message_type";
    // whether device is in WIFI range of a scanner device
    private static final String PARAM_IN_RANGE = "in_range";
    // whether device discovery status has changed (in_range value changed)
    private static final String PARAM_STATUS_CHANGE = "status_change";
    // GCM message that updates device discovery status
    private static final String TYPE_DISCOVERY_STATUS = "discovery_status";

    public enum State {
        UNREGISTERED,
        REGISTERING,
        REGISTERED,
        DEREGISTERING
    }

    public interface ScannerClientListener {
        void onClientSignalDiscovered();

        void onClientSignalLost();
    }

    public interface ScannerClientRegistrationListener {
        void onClientRegistered();

        void onClientRegistrationFailed(String error);
    }

    private State state = State.UNREGISTERED;
    private final Context context;
    private final GcmMessageProxy proxy;
    private final ScannerClientListener listener;
    private final ScannerClientRegistrationListener registrationListener;

    public ScannerClient(Context context, GcmMessageProxy proxy,
                         ScannerClientListener listener, ScannerClientRegistrationListener registrationListener) {
        this.context = context;
        this.proxy = proxy;
        this.listener = listener;
        this.registrationListener = registrationListener;
    }

    public synchronized void register() {
        if (state == State.UNREGISTERED) {
            state = State.REGISTERING;

            LocalBroadcastManager.getInstance(context).registerReceiver(localReceiver,
                    GcmDiscoveryMessages.getIntentFilter());
            final Intent intent = new Intent(context, DiscoveryRegistrationIntentService.class);
            intent.setAction(DiscoveryRegistrationIntentService.ACTION_REGISTER);
            context.startService(intent);
        }
    }

    public synchronized void unregister() {
        if (state == State.REGISTERED) {
            state = State.DEREGISTERING;

            final Intent intent = new Intent(context, DiscoveryRegistrationIntentService.class);
            intent.setAction(DiscoveryRegistrationIntentService.ACTION_UNREGISTER);
            context.startService(intent);
        }
    }

    public synchronized State getState() {
        return state;
    }

    private synchronized void setState(State state) {
        this.state = state;
    }

    private final GcmMessageListener gcmListener = new GcmMessageListener() {
        @Override
        public boolean onMessageReceived(String from, Bundle data) {
            Log.d(TAG, "Processing gcm message from:" + from + " data:" + data);
            final String messageType = data.getString(PARAM_MESSAGE_TYPE);

            if (TYPE_DISCOVERY_STATUS.equals(messageType)) {
                processDiscoveryStatusMessage(data);
                return true;
            }

            return false;
        }

        /**
         * Processes messages containing device discovery info
         *
         * @param data message body
         */
        private void processDiscoveryStatusMessage(Bundle data) {
            final boolean enabled = data != null && data.getString(PARAM_IN_RANGE, "false").equals("true");
            final boolean statusChanged = data != null && data.getString(PARAM_STATUS_CHANGE, "false").equals("true");

            // show notification only when status changes or if registered when user was already in store
            if (statusChanged || enabled) {
                if (enabled) {
                    listener.onClientSignalDiscovered();
                } else {
                    listener.onClientSignalLost();
                }
            }
        }
    };

    private final BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (GcmDiscoveryMessages.WS_REGISTRATION_SUCCEEDED.equals(action)) {
                Log.d(TAG, "Scanner client registered");
                setState(State.REGISTERED);
                proxy.registerListener(gcmListener);
                registrationListener.onClientRegistered();
            } else if (GcmDiscoveryMessages.WS_REGISTRATION_FAILED.equals(action)) {
                Log.d(TAG, "Scanner client registration failed");
                setState(State.UNREGISTERED);
                registrationListener.onClientRegistrationFailed("Failed to register for discovery events");
            } else if (GcmDiscoveryMessages.WS_DEREGISTRATION_SUCCEEDED.equals(action)) {
                Log.d(TAG, "Scanner client unregistered");
                setState(State.UNREGISTERED);
                proxy.unregisterListener(gcmListener);
            } else if (GcmDiscoveryMessages.WS_DEREGISTRATION_FAILED.equals(action)) {
                Log.d(TAG, "Scanner client deregistration failed");
                setState(State.REGISTERED);
                proxy.unregisterListener(gcmListener);
            }
        }
    };
}
