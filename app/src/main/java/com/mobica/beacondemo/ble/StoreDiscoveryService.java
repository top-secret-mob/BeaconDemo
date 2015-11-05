package com.mobica.beacondemo.ble;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.mobica.beacondemo.BeaconApplication;
import com.mobica.beacondemo.MainActivity;
import com.mobica.beacondemo.R;

public class StoreDiscoveryService extends IntentService {
    private static final String ACTION_STORE_ENTRY = "ACTION_STORE_ENTRY";
    private static final String ACTION_STORE_EXIT = "ACTION_STORE_EXIT";
    private static final int NOTIFICATION_ID = 123;
    /**
     * Request code used by notification about store entrance
     */
    private final static int STORE_ENTRANCE_REQUEST = 9002;
    /**
     * Request code used by notification about store exit
     */
    private final static int STORE_EXIT_REQUEST = 9003;
    private boolean wasBleEnabled = false;

    public StoreDiscoveryService() {
        super(StoreDiscoveryService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wasBleEnabled = BleAdapter.isBleEnabled(this);
    }

    public static void registerEntrance() {
        final Intent intent = new Intent(BeaconApplication.getAppContext(), StoreDiscoveryService.class);
        intent.setAction(ACTION_STORE_ENTRY);
        BeaconApplication.getAppContext().startService(intent);
    }

    public static void registerExit() {
        final Intent intent = new Intent(BeaconApplication.getAppContext(), StoreDiscoveryService.class);
        intent.setAction(ACTION_STORE_EXIT);
        BeaconApplication.getAppContext().startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();

        if (ACTION_STORE_ENTRY.equals(action)) {
            BleAdapter.enableBle(this);
            sendEntranceNotification();
        } else if (ACTION_STORE_EXIT.equals(action)) {
            if (!wasBleEnabled) {
                BleAdapter.disableBle(this);
            }
            sendExitNotification();
        }
    }

    private void sendEntranceNotification() {
        sendNotification(STORE_ENTRANCE_REQUEST, "Welcome to store",
                "You've entered our store", R.drawable.ic_exit_to_app_white_24dp);
    }

    private void sendExitNotification() {
        sendNotification(STORE_EXIT_REQUEST, "See you soon",
                "Thank you for visiting our store", R.drawable.ic_exit_to_app_white_24dp);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     */
    private void sendNotification(int requestCode, String title, String message, int iconResId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
