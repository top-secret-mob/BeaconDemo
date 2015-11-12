package com.mobica.discoverysdk.nfc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * General NFC tag
 */
public abstract class NfcTag implements Parcelable {
    private final String storeId;

    protected NfcTag(String storeId) {
        this.storeId = storeId;
    }

    protected NfcTag(Parcel parcel) {
        this.storeId = parcel.readString();
    }

    public String getStoreId() {
        return storeId;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(storeId);
    }
}
