package com.mobica.discoverysdk.wifi;

import android.os.Bundle;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mobica.discoverysdk.gcm.GcmMessageListener;
import com.mobica.discoverysdk.gcm.GcmMessageProxy;
import com.mobica.repositorysdk.RepositoryAdapter;
import com.mobica.repositorysdk.model.WsResponse;
import com.mobica.repositorysdk.utils.FluentExecutors;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

/**
 * WIFI passive scanner client
 */
public class ScannerClient {
    private static final String TAG = ScannerClient.class.getSimpleName();
    // GCM message identifier
    private static final String PARAM_MESSAGE_ID = "message_id";
    // whether device is in WIFI range of a scanner device
    private static final String PARAM_IN_RANGE = "in_range";
    // whether device discovery status has changed (in_range value changed)
    private static final String PARAM_STATUS_CHANGE = "status_change";
    // GCM message that updates device discovery status
    private static final String TYPE_DISCOVERY_STATUS = "discovery_status";

    public interface ScannerClientListener {
        void onClientSignalDiscovered();

        void onClientSignalLost();
    }

    public interface ScannerClientRegistrationListener {
        void onClientRegistered();

        void onClientRegistrationFailed(String error);
    }

    private final List<ScannerClientListener> detectionListeners = new CopyOnWriteArrayList<>();
    private ListenableFuture<WsResponse> SubscribeFuture;
    private ListenableFuture<WsResponse> unsubscribeFuture;

    @Inject
    RepositoryAdapter repositoryAdapter;
    @Inject
    GcmMessageProxy proxy;

    public synchronized void register(final ScannerClientRegistrationListener registrationListener,
                                      final ScannerClientListener detectionListener) {
        if (SubscribeFuture == null) {
            unsubscribeFuture = null;
            SubscribeFuture = repositoryAdapter.subscribeForDiscoveryEvents();
            Futures.addCallback(SubscribeFuture, new FutureCallback<WsResponse>() {
                @Override
                public void onSuccess(WsResponse result) {
                    Log.d(TAG, "Scanner client registered");
                    proxy.registerListener(gcmListener);
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d(TAG, "Scanner client registration failed");
                }
            }, FluentExecutors.mainThreadExecutor());
        }

        Futures.addCallback(SubscribeFuture, new FutureCallback<WsResponse>() {
            @Override
            public void onSuccess(WsResponse result) {
                registrationListener.onClientRegistered();
                detectionListeners.add(detectionListener);
            }

            @Override
            public void onFailure(Throwable t) {
                registrationListener.onClientRegistrationFailed("Failed to register for discovery events");
            }
        }, FluentExecutors.mainThreadExecutor());
    }

    public synchronized void unregister(final ScannerClientListener detectionListener) {
        detectionListeners.remove(detectionListener);

        if (detectionListeners.isEmpty() && unsubscribeFuture == null && SubscribeFuture != null) {
            SubscribeFuture = null;
            unsubscribeFuture = repositoryAdapter.subscribeForDiscoveryEvents();
            Futures.addCallback(unsubscribeFuture, new FutureCallback<WsResponse>() {
                @Override
                public void onSuccess(WsResponse result) {
                    Log.d(TAG, "Scanner client unregistered");
                    proxy.unregisterListener(gcmListener);
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d(TAG, "Scanner client deregistration failed");
                    proxy.unregisterListener(gcmListener);
                }
            }, FluentExecutors.mainThreadExecutor());
        }
    }

    private final GcmMessageListener gcmListener = new GcmMessageListener() {
        @Override
        public boolean onMessageReceived(String from, Bundle data) {
            Log.d(TAG, "Processing gcm message from:" + from + " data:" + data);
            final String messageType = data.getString(PARAM_MESSAGE_ID);

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
            if (detectionListeners.isEmpty()) {
                return;
            }

            final boolean enabled = data != null && data.getString(PARAM_IN_RANGE, "false").equals("true");
            final boolean statusChanged = data != null && data.getString(PARAM_STATUS_CHANGE, "false").equals("true");

            // show notification only when status changes or if registered when user was already in store
            if (statusChanged || enabled) {
                for (ScannerClientListener listener : detectionListeners) {
                    if (enabled) {
                        listener.onClientSignalDiscovered();
                    } else {
                        listener.onClientSignalLost();
                    }
                }
            }
        }
    };
}
