package com.mobica.beacondemo.nfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobica.beacondemo.R;
import com.mobica.beacondemo.ble.BleAdapter;

public class DiscoverActivity extends AppCompatActivity implements NfcParserListener {
    private static final String TAG = DiscoverActivity.class.getSimpleName();
    private static final String NFC_SCHEME = "tmnfc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        final Intent intent = getIntent();
        if (intent != null) {
            handleIntent(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            handleIntent(intent);
        }
    }

    @Override
    public void onNfcTagParsed(NfcTag tag) {
        if (tag instanceof EntranceTag) {
            Snackbar.make(findViewById(R.id.snackbarPosition),
                    "Welcome to '" + tag.getStoreId() + "' store", Snackbar.LENGTH_LONG).show();
            Log.d(TAG, "Enabling BLE ");
            BleAdapter.enableBle(this);
        } else if (tag instanceof ItemTag) {
            Snackbar.make(findViewById(R.id.snackbarPosition),
                    "Checkout our brand new item '" + ((ItemTag) tag).getItemId() + "'!", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNfcTagParsingFailed(String error) {
        Log.e(TAG, "Nfc tag parsing error: " + error);
    }

    private void handleIntent(Intent intent) {
        final String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            final String scheme = intent.getScheme();
            // skip unsupported tags
            if (NFC_SCHEME.equals(scheme)) {
                Log.d(TAG, "Scheme: " + intent.getData());
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask(this).execute(tag);
            }
        }
    }
}
