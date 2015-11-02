package com.mobica.beacondemo.nfc;

/**
 * Created by woos on 2015-11-02.
 */
public interface NfcParserListener {

    void onNfcTagParsed(NfcTag tag);

    void onNfcTagParsingFailed(String error);
}
