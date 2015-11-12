package com.mobica.discoverysdk.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Wifi scanning providing class
 */
public class WifiScanner {
    private final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
    private final WifiManager wifiManager;
    private final WifiScannerListener listener;
    private final String ssidPattern;
    private final long scanningFrequency;
    private final long outOfRangeTimeout;
    private Map<String, Long> detectedAp = new HashMap<>();
    private Context context;
    private ScheduledFuture<?> scanningFuture = null;

    public WifiScanner(Context context, WifiScannerListener listener, String ssidPattern,
                       long scanningFrequency, long outOfRangeTimeout) {
        this.context = context;
        this.listener = listener;
        this.ssidPattern = ssidPattern.toLowerCase();
        this.scanningFrequency = scanningFrequency;
        this.outOfRangeTimeout = TimeUnit.SECONDS.toMillis(outOfRangeTimeout);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public synchronized boolean startScanner() {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            context.registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            this.scanningFuture = threadPool.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    wifiManager.startScan();
                }
            }, 0, scanningFrequency, TimeUnit.SECONDS);

            return true;
        }

        return false;
    }

    public synchronized void stopScanner() {
        if (scanningFuture != null) {
            context.unregisterReceiver(scanReceiver);

            scanningFuture.cancel(true);
        }
    }

    public synchronized boolean isActive() {
        return scanningFuture != null;
    }

    public interface WifiScannerListener {
        void onWifiAccessPointDetected(String ssid);

        void onWifiAccessPointLost(String ssid);
    }

    /**
     * Called  when single wifi access point has been detected
     *
     * @param ssid SSID of a detected Access Point
     */
    private synchronized void onAccessPointFound(String ssid) {
        if (!detectedAp.containsKey(ssid)) {
            listener.onWifiAccessPointDetected(ssid);
        }
        detectedAp.put(ssid, System.currentTimeMillis());
    }

    /**
     * Called  when single wifi scanning round is complete
     */
    private synchronized void onScanComplete() {
        for (String ssid : detectedAp.keySet()) {
            final long inRangeTimestamp = detectedAp.get(ssid);

            if ((System.currentTimeMillis() - inRangeTimestamp) > outOfRangeTimeout) {
                detectedAp.remove(ssid);
                listener.onWifiAccessPointLost(ssid);
            }
        }
    }

    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final List<ScanResult> results = wifiManager.getScanResults();

            for (ScanResult result : results) {
                final String ssid = result.SSID != null ? result.SSID.toLowerCase() : null;
                if (ssid != null && ssid.contains(ssidPattern)) {
                    onAccessPointFound(ssid);
                    break;
                }
            }

            onScanComplete();
        }
    };
}
