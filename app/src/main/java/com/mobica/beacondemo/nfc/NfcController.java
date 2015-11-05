package com.mobica.beacondemo.nfc;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

/**
 * NFC features controller
 */
public class NfcController {
    /**
     * Broadcast message action triggered when NFC tag has been recognized
     */
    public static final String ACTION_NFC_TAG_DISCOVERED = "ACTION_NFC_TAG_DISCOVERED";
    /**
     * Extra parameters holding reference to {@link NfcTag}
     */
    public static final String EXTRA_TAG = "EXTRA_TAG";

    public static boolean isNfcAvailable(Context context) {
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        return (adapter != null && adapter.isEnabled());
    }
}
