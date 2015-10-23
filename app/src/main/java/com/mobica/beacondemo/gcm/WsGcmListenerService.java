package com.mobica.beacondemo.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.mobica.beacondemo.MainActivity;
import com.mobica.beacondemo.R;

public class WsGcmListenerService extends GcmListenerService {
    private static final String TAG = WsGcmListenerService.class.getSimpleName();

    public WsGcmListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Log.d(TAG, "RECEIVED: from:" + from + " data:" + data);
        final boolean enabled = data != null && data.getString("bt_enabled", "false").equals("true");
        final boolean statusChanged = data != null && data.getString("status_change", "false").equals("true");

        final Intent intent = new Intent(GcmPreferences.WS_MESSAGE);
        intent.putExtra(GcmPreferences.EXTRA_BT_STATE, enabled);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        // show notification only when status changes or when when registered already in store
        if (statusChanged || enabled) {
            sendNotification(enabled);
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     */
    private void sendNotification(boolean enteredStore) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_exit_to_app_white_24dp)
                .setContentTitle(enteredStore ? "Welcome to store" : "See you soon")
                .setContentText(enteredStore ? "You've entered into our store" : "Thank you for visiting our store")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
