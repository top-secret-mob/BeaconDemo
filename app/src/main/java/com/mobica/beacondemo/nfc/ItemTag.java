package com.mobica.beacondemo.nfc;

/**
 * Created by woos on 2015-11-02.
 */
public class ItemTag extends NfcTag {
    private final String itemId;

    public ItemTag(String storeId, String itemId) {
        super(storeId);
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }
}
