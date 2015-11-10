package com.mobica.beacondemo.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.common.base.Strings;
import com.mobica.beacondemo.ble.DiscoveryMode;
import com.mobica.beacondemo.ble.StoreDiscoveryService;

public class WsGcmListenerService extends GcmListenerService {
    private static final String TAG = WsGcmListenerService.class.getSimpleName();
    // GCM message identifier
    private static final String PARAM_MESSAGE_TYPE = "message_type";
    // whether device is in WIFI range of a scanner device
    private static final String PARAM_IN_RANGE = "in_range";
    // whether device discovery status has changed (in_range value changed)
    private static final String PARAM_STATUS_CHANGE = "status_change";
    // GCM message that updates device discovery status
    private static final String TYPE_DISCOVERY_STATUS = "discovery_status";

    public WsGcmListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Log.d(TAG, "RECEIVED: from:" + from + " data:" + data);
        final String messageType = data.getString(PARAM_MESSAGE_TYPE);
        if (Strings.isNullOrEmpty(messageType)) {
            Log.e(TAG, "Received incorrect GCM message");
            return;
        }

        if (TYPE_DISCOVERY_STATUS.equals(messageType)) {
            processDiscoveryStatusMessage(data);
        } else {
            Log.e(TAG, "Received unidentified GCM message");
        }
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
                StoreDiscoveryService.registerEntrance(DiscoveryMode.WIFI_PASSIVE);
            } else {
                StoreDiscoveryService.registerExit(DiscoveryMode.WIFI_PASSIVE);
            }
        }
    }
}
