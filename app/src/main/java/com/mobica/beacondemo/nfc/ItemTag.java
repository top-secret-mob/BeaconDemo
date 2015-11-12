package com.mobica.beacondemo.nfc;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobica.discoverysdk.nfc.NfcTag;

/**
 * Represents NFC tag placed next to item in store
 */
public class ItemTag extends NfcTag {
    private final String itemId;

    public ItemTag(String storeId, String itemId) {
        super(storeId);
        this.itemId = itemId;
    }

    public ItemTag(Parcel parcel) {
        super(parcel);
        this.itemId = parcel.readString();
    }

    public String getItemId() {
        return itemId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(itemId);
    }

    public static final Parcelable.Creator<ItemTag> CREATOR = new Parcelable.Creator<ItemTag>() {
        public ItemTag createFromParcel(Parcel in) {
            return new ItemTag(in);
        }

        public ItemTag[] newArray(int size) {
            return new ItemTag[size];
        }
    };
}
