package com.mobica.discoverysdk.gcm;

import android.os.Bundle;

/**
 * Created by woos on 2015-11-12.
 */
public interface GcmMessageListener {

    boolean onMessageReceived(String from, Bundle data);
}
