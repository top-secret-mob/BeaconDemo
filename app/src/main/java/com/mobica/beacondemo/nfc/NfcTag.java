package com.mobica.beacondemo.nfc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * General NFC tag
 */
abstract class NfcTag implements Parcelable {
    private final String storeId;

    NfcTag(String storeId) {
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
