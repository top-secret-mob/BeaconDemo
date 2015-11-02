package com.mobica.beacondemo.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by woos on 2015-11-02.
 */
public class BleAdapter {

    public static boolean enableBle(Context context) {
        return switchBle(context, true);
    }

    public static boolean disableBle(Context context) {
        return switchBle(context, false);
    }

    public static boolean isBleSupported(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private static boolean switchBle(Context context, boolean enable) {
        if (!isBleSupported(context)) {
            return false;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            boolean isEnabled = bluetoothAdapter.isEnabled();
            if (enable && !isEnabled) {
                return bluetoothAdapter.enable();
            } else if (!enable && isEnabled) {
                return bluetoothAdapter.disable();
            }
            // No need to change bluetooth state
            return true;
        }
        // No BLE
        return false;
    }
}
