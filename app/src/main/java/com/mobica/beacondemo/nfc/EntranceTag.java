package com.mobica.beacondemo.nfc;

import android.os.Parcel;

/**
 * Represents NFC tag placed next to store entrance
 */
public class EntranceTag extends NfcTag {

    public EntranceTag(String storeId) {
        super(storeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
