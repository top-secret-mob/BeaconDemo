package com.mobica.discoverysdk.nfc;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mobica.discoverysdk.dagger.Graphs;

import javax.inject.Inject;

/**
 * A non-ui Activity being launched every time an NFC tag is discovered.
 * Starts {@link NfcDiscoverService} that process provided NFC tag
 */
public class DiscoverActivity extends AppCompatActivity {
    @Inject
    Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Graphs.inject(this);

        final Intent intent = getIntent();
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            final Intent nfcIntent = new Intent(appContext, NfcDiscoverService.class);
            nfcIntent.putExtras(intent.getExtras());
            startService(nfcIntent);
        }

        finish();
    }
}