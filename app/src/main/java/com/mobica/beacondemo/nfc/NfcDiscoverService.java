package com.mobica.beacondemo.nfc;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.common.base.Strings;
import com.mobica.beacondemo.ble.StoreDiscoveryService;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for discovering NFC tags
 */
public class NfcDiscoverService extends IntentService {
    private static final String TAG = NfcDiscoverService.class.getSimpleName();
    // identifies store entrance tag
    private static final Pattern ENTRANCE_PATTERN = Pattern.compile("/*store/(\\w+)/entrance");
    // identifies store exit tag
    private static final Pattern EXIT_PATTERN = Pattern.compile("/*store/(\\w+)/exit");
    // identifies store item (i.e. phone on a shelf)
    private static final Pattern ITEM_PATTERN = Pattern.compile("/*store/(\\w+)/item/(\\w+)");

    public NfcDiscoverService() {
        super(NfcDiscoverService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            // no NFC tag found
            return;
        }

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // non-NDEF tag, skipping
            return;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN
                    && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI)) {
                final Uri uri = readUri(ndefRecord);
                Log.d(TAG, "Detected NFC tag with path: " + uri.getEncodedPath());

                final NfcTag nfcTag = parsePath(uri.getEncodedPath());
                if (nfcTag != null) {
                    if (nfcTag instanceof EntranceTag) {
                        StoreDiscoveryService.registerEntrance();
                    } else if (nfcTag instanceof ExitTag) {
                        StoreDiscoveryService.registerExit();
                    } else {
                        final Intent nfcIntent = new Intent(NfcController.ACTION_NFC_TAG_DISCOVERED);
                        nfcIntent.putExtra(NfcController.EXTRA_TAG, nfcTag);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(nfcIntent);
                    }
                } else {
                    Log.d(TAG, "Tag has not been correctly recognized");
                }
            }
        }
    }

    private Uri readUri(NdefRecord record) {
        int tnf = record.getTnf();
        if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
            return Uri.parse(new String(record.getType()));
        }

        final byte[] payload = record.getPayload();
        return Uri.parse(new String(payload, 1, payload.length - 1));
    }

    private NfcTag parsePath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return null;
        }

        // entrance tag
        if (ENTRANCE_PATTERN.matcher(path).matches()) {
            return parseEntranceTag(path);
        } else if (EXIT_PATTERN.matcher(path).matches()) {
            return parseExitTag(path);
        } else if (ITEM_PATTERN.matcher(path).matches()) {
            return parseItemTag(path);
        }

        return null;
    }

    private NfcTag parseEntranceTag(String path) {
        final Matcher matcher = ENTRANCE_PATTERN.matcher(path);

        if (matcher.find() && matcher.groupCount() > 0) {
            return new EntranceTag(matcher.group(1));
        }
        return null;
    }

    private NfcTag parseExitTag(String path) {
        final Matcher matcher = EXIT_PATTERN.matcher(path);

        if (matcher.find() && matcher.groupCount() > 0) {
            return new ExitTag(matcher.group(1));
        }
        return null;
    }

    private NfcTag parseItemTag(String path) {
        final Matcher matcher = ITEM_PATTERN.matcher(path);

        if (matcher.find() && matcher.groupCount() > 0) {
            return new ItemTag(matcher.group(1), matcher.group(2));
        }
        return null;
    }
}
