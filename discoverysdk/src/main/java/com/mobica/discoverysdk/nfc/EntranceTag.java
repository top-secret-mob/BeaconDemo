package com.mobica.discoverysdk.nfc;

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
