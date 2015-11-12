package com.mobica.discoverysdk.gcm;

import android.os.Bundle;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;

/**
 * Created by woos on 2015-11-12.
 */
public class GcmMessageProxy {
    private final List<GcmMessageListener> listeners = new CopyOnWriteArrayList<>();

    @Inject
    public GcmMessageProxy() {
    }

    public final boolean onMessageReceived(String from, Bundle data) {
        boolean consumedByAny = false;

        for (GcmMessageListener listener : listeners) {
            try {
                boolean consumed = listener.onMessageReceived(from, data);
                consumedByAny = consumed || consumedByAny;
            } catch (Exception ignore) {
            }
        }

        return consumedByAny;
    }

    public void registerListener(GcmMessageListener listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(GcmMessageListener listener) {
        this.listeners.remove(listener);
    }
}
