package com.mobica.discoverysdk.utils;

import android.util.Log;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Hardware utils
 */
public class HwUtils {
    private static final String TAG = HwUtils.class.getSimpleName();
    private static final String WIFI_IF_PREFIX = "wlan0";

    /**
     * Retrieves Wifi adapter MAC address
     */
    public static String getWifiMacAddress() {
        try {
            final List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(WIFI_IF_PREFIX)) {
                    continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    continue;
                }

                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02x:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }

                return buf.toString();
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to read network interfaces");
        }
        return null;
    }
}
