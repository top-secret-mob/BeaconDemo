package com.mobica.discoverysdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.mobica.discoverysdk.gcm.GcmMessageProxy;
import com.mobica.discoverysdk.geofence.GeofenceProvider;
import com.mobica.discoverysdk.nfc.NfcController;
import com.mobica.discoverysdk.wifi.ScannerClient;
import com.mobica.discoverysdk.wifi.WifiScanner;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Store discovery API
 */
public class DiscoveryClient implements NfcController.NfcDiscoveryListener, WifiScanner.WifiScannerListener,
        ScannerClient.ScannerClientListener, ScannerClient.ScannerClientRegistrationListener,
        GeofenceProvider.GeofenceProviderListener {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final NfcController nfcController;
    private final WifiScanner wifiScanner;
    private final ScannerClient scannerClient;
    private final GeofenceProvider geofenceProvider;
    private DiscoveryClientListener clientListener;
    private DiscoveryConnectionListener connectionListener;
    private final AtomicInteger ssidCounter = new AtomicInteger();

    private DiscoveryClient(Builder builder) {
        if (builder.modes.contains(DiscoveryMode.NFC)) {
            nfcController = new NfcController(builder.context, this);
        } else {
            nfcController = null;
        }

        if (builder.modes.contains(DiscoveryMode.WIFI_ACTIVE)) {
            wifiScanner = new WifiScanner(builder.context, this, builder.ssidPattern,
                    builder.wifiScanningFrequency, builder.wifiOutOfRangeTimeout);
        } else {
            wifiScanner = null;
        }

        if (builder.modes.contains(DiscoveryMode.WIFI_PASSIVE)) {
            scannerClient = new ScannerClient(builder.context, builder.gcmProxy, this, this);
        } else {
            scannerClient = null;
        }

        if (builder.modes.contains(DiscoveryMode.GEOFENCING)) {
            geofenceProvider = new GeofenceProvider(this);
        } else {
            geofenceProvider = null;
        }

        this.clientListener = builder.clientListener;
        this.connectionListener = builder.connectionListener;
    }

    public void connect() {
        if (nfcController != null) {
            if (nfcController.startMonitoring()) {
                onModeEnabled(DiscoveryMode.NFC);
            } else {
                onModeEnablingFailed(DiscoveryMode.NFC, "Nfc adapter is disabled");
            }
        }

        if (wifiScanner != null) {
            if (wifiScanner.startScanner()) {
                onModeEnabled(DiscoveryMode.WIFI_ACTIVE);
            } else {
                onModeEnablingFailed(DiscoveryMode.WIFI_ACTIVE, "Location service is disabled");
            }
        }

        if (scannerClient != null) {
            scannerClient.register();
        }

        if (geofenceProvider != null) {
            geofenceProvider.connect();
        }
    }

    public void disconnect() {
        if (nfcController != null) {
            nfcController.stopMonitoring();
        }

        if (wifiScanner != null) {
            wifiScanner.stopScanner();
        }

        if (scannerClient != null) {
            scannerClient.unregister();
        }

        if (geofenceProvider != null) {
            geofenceProvider.disconnect();
        }
    }

    // NFC tag
    @Override
    public void onEntranceTagDiscovered() {
        onStoreDiscovered(DiscoveryMode.NFC);
    }

    // NFC tag
    @Override
    public void onExitTagDiscovered() {
        onStoreLost(DiscoveryMode.NFC);
    }

    @Override
    public void onWifiAccessPointDetected(String ssid) {
        if (ssidCounter.incrementAndGet() == 1) {
            onStoreDiscovered(DiscoveryMode.WIFI_ACTIVE);
        }
    }

    @Override
    public void onWifiAccessPointLost(String ssid) {
        if (ssidCounter.decrementAndGet() == 0) {
            onStoreLost(DiscoveryMode.WIFI_ACTIVE);
        }
    }

    @Override
    public void onClientSignalDiscovered() {
        onStoreDiscovered(DiscoveryMode.WIFI_PASSIVE);
    }

    @Override
    public void onClientSignalLost() {
        onStoreLost(DiscoveryMode.WIFI_PASSIVE);
    }

    @Override
    public void onClientRegistered() {
        onModeEnabled(DiscoveryMode.WIFI_PASSIVE);
    }

    @Override
    public void onClientRegistrationFailed(String error) {
        onModeEnablingFailed(DiscoveryMode.WIFI_PASSIVE, error);
    }

    @Override
    public void onGeofenceApiConnected() {
        onModeEnabled(DiscoveryMode.GEOFENCING);
    }

    @Override
    public void onGeofenceApiConnectionFailed(String error) {
        onModeEnablingFailed(DiscoveryMode.GEOFENCING, error);
    }

    @Override
    public void onGeofenceEntered() {
        onStoreDiscovered(DiscoveryMode.GEOFENCING);
    }

    @Override
    public void onGeofenceExit() {
        onStoreLost(DiscoveryMode.GEOFENCING);
    }

    private void onStoreDiscovered(final DiscoveryMode mode) {
        if (clientListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    clientListener.onStoreInRange(mode);
                }
            });
        }
    }

    private void onStoreLost(final DiscoveryMode mode) {
        if (clientListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    clientListener.onStoreOutOfRange(mode);
                }
            });
        }
    }

    private void onModeEnabled(final DiscoveryMode mode) {
        if (connectionListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectionListener.onModeEnabled(mode);
                }
            });
        }
    }

    private void onModeEnablingFailed(final DiscoveryMode mode, final String error) {
        if (connectionListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectionListener.onModeEnablingFailed(mode, error);
                }
            });
        }
    }

    public interface DiscoveryClientListener {
        void onStoreInRange(DiscoveryMode mode);

        void onStoreOutOfRange(DiscoveryMode mode);
    }

    public interface DiscoveryConnectionListener {
        void onModeEnabled(DiscoveryMode mode);

        void onModeEnablingFailed(DiscoveryMode mode, String error);
    }

    public static class Builder {
        private final EnumSet<DiscoveryMode> modes = EnumSet.noneOf(DiscoveryMode.class);
        private final Context context;
        private DiscoveryClientListener clientListener;
        private DiscoveryConnectionListener connectionListener;
        private long wifiScanningFrequency;
        private long wifiOutOfRangeTimeout;
        private String ssidPattern;
        private GcmMessageProxy gcmProxy;

        public Builder(Context context) {
            this.context = context;

            this.wifiScanningFrequency = context.getResources().getInteger(R.integer.wifi_default_scanning_rate);
            this.wifiOutOfRangeTimeout = context.getResources().getInteger(R.integer.wifi_default_lost_timeout);
            this.ssidPattern = context.getString(R.string.wifi_ssid_pattern);
        }

        public Builder addDiscoveryMode(DiscoveryMode mode) {
            modes.add(mode);
            return this;
        }

        public Builder setWifiScanningFrequency(long frequency) {
            this.wifiScanningFrequency = frequency;
            return this;
        }

        public Builder setWifiOutOfRangeTimeout(long timeout) {
            this.wifiOutOfRangeTimeout = timeout;
            return this;
        }

        public Builder setWifiSsidPattern(String ssidPattern) {
            this.ssidPattern = ssidPattern;
            return this;
        }

        public Builder setDiscoveryListener(DiscoveryClientListener clientListener) {
            this.clientListener = clientListener;
            return this;
        }

        public Builder setDiscoveryConnectionListener(DiscoveryConnectionListener listener) {
            this.connectionListener = listener;
            return this;
        }

        public Builder setGcmProxy(GcmMessageProxy gcmProxy) {
            this.gcmProxy = gcmProxy;
            return this;
        }

        public DiscoveryClient build() {
            if (modes.contains(DiscoveryMode.WIFI_PASSIVE) && gcmProxy == null) {
                throw new IllegalArgumentException("GCM proxy must be specified for " + DiscoveryMode.WIFI_PASSIVE);
            }

            return new DiscoveryClient(this);
        }
    }
}
