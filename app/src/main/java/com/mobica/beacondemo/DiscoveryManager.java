package com.mobica.beacondemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mobica.discoverysdk.DiscoveryClient;
import com.mobica.discoverysdk.DiscoveryMode;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages the state of different device discovery modes
 */
@Singleton
public class DiscoveryManager implements DiscoveryClient.DiscoveryClientListener,
        DiscoveryClient.DiscoveryConnectionListener {
    private static final String TAG = DiscoveryManager.class.getSimpleName();
    private static final int NOTIFICATION_ID = 123;
    /**
     * Request code used by notification about store entrance
     */
    private final static int STORE_ENTRANCE_REQUEST = 9002;
    /**
     * Request code used by notification about store exit
     */
    private final static int STORE_EXIT_REQUEST = 9003;

    @Inject
    Context context;

    private DiscoveryClient client;
    private EnumSet<DiscoveryMode> discovers = EnumSet.noneOf(DiscoveryMode.class);

    public void updateModes(EnumSet<DiscoveryMode> activeModes) {
        Log.d(TAG, "Updating modes to: " + activeModes);

        if (client != null) {
            client.disconnect();
            client = null;
        }

        if (!activeModes.isEmpty()) {
            final DiscoveryClient.Builder builder = new DiscoveryClient.Builder(context)
                    .setDiscoveryConnectionListener(this)
                    .setDiscoveryListener(this);

            if (activeModes.contains(DiscoveryMode.NFC)) {
                builder.addDiscoveryMode(DiscoveryMode.NFC);
            }

            if (activeModes.contains(DiscoveryMode.WIFI_ACTIVE)) {
                builder.addDiscoveryMode(DiscoveryMode.WIFI_ACTIVE)
                        .setWifiScanningFrequency(context.getResources().getInteger(R.integer.wifi_scanning_rate))
                        .setWifiOutOfRangeTimeout(context.getResources().getInteger(R.integer.wifi_lost_timeout));
            }

            if (activeModes.contains(DiscoveryMode.WIFI_PASSIVE)) {
                builder.addDiscoveryMode(DiscoveryMode.WIFI_PASSIVE);
            }

            if (activeModes.contains(DiscoveryMode.GEOFENCING)) {
                builder.addDiscoveryMode(DiscoveryMode.GEOFENCING);
            }

            client = builder.build();
            client.connect();
        }
    }

    @Override
    public void onStoreInRange(DiscoveryMode mode) {
        Log.d(TAG, "Found store (" + mode + ")");
        if (discovers.isEmpty()) {
            sendEntranceNotification();
//            BleAdapter.enableBle(context);
        }
        discovers.add(mode);
    }

    @Override
    public void onStoreOutOfRange(DiscoveryMode mode) {
        Log.d(TAG, "Store lost (" + mode + ")");
        discovers.remove(mode);
        if (discovers.isEmpty()) {
            sendExitNotification();
//            BleAdapter.disableBle(context);
        }
    }

    @Override
    public void onModeEnabled(DiscoveryMode mode) {
        Log.d(TAG, "Mode enabled: " + mode);
    }

    @Override
    public void onModeEnablingFailed(DiscoveryMode mode, String error) {
        Log.e(TAG, "Mode enabling failed: " + mode + " reason: " + error);
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
        Intent intent = new Intent(context, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(iconResId)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
