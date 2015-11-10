package com.mobica.beacondemo.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.mobica.beacondemo.R;
import com.mobica.beacondemo.ble.DiscoveryMode;
import com.mobica.beacondemo.ble.StoreDiscoveryService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Wifi scanning providing class
 */
@Singleton
public class WifiScanner {
    private final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
    private final WifiManager wifiManager;
    private final String ssidPattern;
    private final long outOfRangeTimeout;
    private final long scanningFrequency;
    private boolean isScanning;
    private boolean inRange;
    private long inRangeTimestamp;
    private Context context;

    @Inject
    public WifiScanner(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ssidPattern = context.getString(R.string.wifi_ssid_pattern).toLowerCase();
        outOfRangeTimeout = TimeUnit.SECONDS.toMillis(context.getResources().getInteger(R.integer.wifi_lost_timeout));
        scanningFrequency = context.getResources().getInteger(R.integer.wifi_scanning_rate);
    }

    public synchronized void startScanner() {
        if (!isScanning) {
            if (wifiManager.isWifiEnabled()) {
                context.registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

                threadPool.scheduleAtFixedRate(new Scanner(), 0, scanningFrequency, TimeUnit.SECONDS);
                isScanning = true;
            }
        }
    }

    public synchronized void stopScanner() {
        if (isScanning) {
            context.unregisterReceiver(scanReceiver);

            threadPool.shutdown();
            isScanning = false;
        }
    }

    public synchronized boolean isActive() {
        return isScanning;
    }

    private class Scanner implements Runnable {
        @Override
        public void run() {
            wifiManager.startScan();
        }
    }

    /**
     * Called  when single wifi scanning round is complete
     *
     * @param apDetected whether WIFI access point has been detected
     */
    private synchronized void onScanComplete(boolean apDetected) {
        if (apDetected) {
            inRangeTimestamp = System.currentTimeMillis();
        }

        if (!inRange && apDetected) {
            inRange = true;
            StoreDiscoveryService.registerEntrance(DiscoveryMode.WIFI_ACTIVE);
        } else if (inRange && !apDetected && (System.currentTimeMillis() - inRangeTimestamp) > outOfRangeTimeout) {
            inRange = false;
            StoreDiscoveryService.registerExit(DiscoveryMode.WIFI_ACTIVE);
        }
    }

    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final List<ScanResult> results = wifiManager.getScanResults();

            for (ScanResult result : results) {
                if (result.SSID != null && result.SSID.toLowerCase().contains(ssidPattern)) {
                    onScanComplete(true);
                    break;
                }
            }

            onScanComplete(false);
        }
    };
}
