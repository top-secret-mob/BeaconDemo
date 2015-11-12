package com.mobica.discoverysdk.nfc;

/**
 * Represents NFC tag placed next to store exit
 */
public class ExitTag extends NfcTag {

    public ExitTag(String storeId) {
        super(storeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
