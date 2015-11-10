package com.mobica.beacondemo.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mobica.beacondemo.BeaconApplication;

/**
 * A non-ui Activity being launched every time an NFC tag is discovered.
 * Starts {@link NfcDiscoverService} that process provided NFC tag
 */
public class DiscoverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            final Intent nfcIntent = new Intent(BeaconApplication.getAppContext(), NfcDiscoverService.class);
            nfcIntent.putExtras(intent.getExtras());
            startService(nfcIntent);
        }

        finish();
    }
}