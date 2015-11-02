package com.mobica.beacondemo.nfc;

import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NdefReaderTask extends AsyncTask<Tag, Void, NfcTag> {
    private static final String TAG = NdefReaderTask.class.getSimpleName();
    private static final Pattern ENTRANCE_PATTERN = Pattern.compile("/*store/(\\w+)/entrance");
    // identifies store item (i.e. phone on a shelf)
    private static final Pattern ITEM_PATTERN = Pattern.compile("/*store/(\\w+)/item/(\\w+)");
    private final NfcParserListener listener;

    public NdefReaderTask(NfcParserListener listener) {
        Preconditions.checkNotNull(listener, "listener must not be null");
        this.listener = listener;
    }

    @Override
    protected NfcTag doInBackground(Tag... params) {
        Tag tag = params[0];

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // non-NDEF tag, skipping
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN
                    && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI)) {
                final Uri uri = readUri(ndefRecord);
                Log.d(TAG, "Detected tag with path: " + uri.getEncodedPath());

                return parsePath(uri.getEncodedPath());
            }
        }

        return null;
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

    private NfcTag parseItemTag(String path) {
        final Matcher matcher = ITEM_PATTERN.matcher(path);

        if (matcher.find() && matcher.groupCount() > 0) {
            return new ItemTag(matcher.group(1), matcher.group(2));
        }
        return null;
    }

    @Override
    protected void onPostExecute(NfcTag result) {
        if (result != null) {
            listener.onNfcTagParsed(result);
        } else {
            listener.onNfcTagParsingFailed("Unrecognized tag");
        }
    }
}