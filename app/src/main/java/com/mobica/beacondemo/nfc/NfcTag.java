package com.mobica.beacondemo.nfc;

/**
 * Created by woos on 2015-11-02.
 */
class NfcTag {
    private final String storeId;

    public NfcTag(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreId() {
        return storeId;
    }
}
