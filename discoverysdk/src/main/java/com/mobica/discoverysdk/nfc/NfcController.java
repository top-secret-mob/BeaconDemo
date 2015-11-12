package com.mobica.discoverysdk.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.support.v4.content.LocalBroadcastManager;

/**
 * NFC features controller
 */
public class NfcController {
    private final Context context;
    private final NfcDiscoveryListener listener;

    public NfcController(Context context, NfcDiscoveryListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public boolean startMonitoring() {
        if (!isNfcEnabled(context)) {
            return false;
        }

        final IntentFilter filter = new IntentFilter(NfcDiscoverService.ENTRANCE_TAG_DISCOVERED);
        filter.addAction(NfcDiscoverService.EXIT_TAG_DISCOVERED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        return true;
    }

    public void stopMonitoring() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    public interface NfcDiscoveryListener {
        void onEntranceTagDiscovered();

        void onExitTagDiscovered();
    }

    public static boolean isNfcEnabled(Context context) {
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        return (adapter != null && adapter.isEnabled());
    }

    /**
     * Broadcast receiver for NFC tags
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (NfcDiscoverService.ENTRANCE_TAG_DISCOVERED.equals(action)) {
                listener.onEntranceTagDiscovered();
            } else if (NfcDiscoverService.EXIT_TAG_DISCOVERED.equals(action)) {
                listener.onExitTagDiscovered();
            }
        }
    };
}
