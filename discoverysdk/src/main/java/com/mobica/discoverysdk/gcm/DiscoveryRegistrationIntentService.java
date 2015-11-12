package com.mobica.discoverysdk.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.mobica.discoverysdk.R;
import com.mobica.discoverysdk.dagger.Graphs;
import com.mobica.discoverysdk.gcm.model.DiscoveryRegisterRequest;
import com.mobica.discoverysdk.gcm.model.DiscoveryUnregisterRequest;
import com.mobica.discoverysdk.gcm.model.WsResponse;
import com.mobica.discoverysdk.utils.HwUtils;
import com.mobica.discoverysdk.volley.GsonRequest;

import javax.inject.Inject;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DiscoveryRegistrationIntentService extends IntentService {
    private static final String TAG = DiscoveryRegistrationIntentService.class.getSimpleName();
    public static final String ACTION_REGISTER = "ACTION_REGISTER";
    public static final String ACTION_UNREGISTER = "ACTION_UNREGISTER";

    @Inject
    RequestQueue requestQueue;

    public DiscoveryRegistrationIntentService() {
        super(DiscoveryRegistrationIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Graphs.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();

        if (ACTION_REGISTER.equals(action)) {
            registerForDiscoveryEvents();
        } else if (ACTION_UNREGISTER.equals(action)) {
            unregisterFromDiscoveryEvents();
        }
    }

    /**
     * Send Gcm token to our service
     */
    private void registerForDiscoveryEvents() {
        final String mac = HwUtils.getWifiMacAddress();
        if (Strings.isNullOrEmpty(mac)) {
            Log.e(TAG, "Failed to retrieve Wifi adapter MAC address");
            sendLocalBroadcast(new Intent(GcmDiscoveryMessages.WS_REGISTRATION_FAILED));
            return;
        }

        final String requestBody = new Gson().toJson(new DiscoveryRegisterRequest(mac));

        GsonRequest<WsResponse> req = new GsonRequest<>(Request.Method.POST,
                getString(R.string.register_api), WsResponse.class, requestBody,
                new Response.Listener<WsResponse>() {
                    @Override
                    public void onResponse(WsResponse response) {
                        Log.d(TAG, "WS subscription finished=" + response.getStatus());

                        if (response.getStatus() == WsResponse.Status.success) {
                            sendLocalBroadcast(new Intent(GcmDiscoveryMessages.WS_REGISTRATION_SUCCEEDED));
                        } else {
                            sendLocalBroadcast(new Intent(GcmDiscoveryMessages.WS_REGISTRATION_FAILED));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "WS subscription failed=" + error.getCause());
                sendLocalBroadcast(new Intent(GcmDiscoveryMessages.WS_REGISTRATION_FAILED));
            }
        });

        requestQueue.add(req);
    }

    /**
     * Send message to web service in order to unregister
     */
    private void unregisterFromDiscoveryEvents() {
        final String mac = HwUtils.getWifiMacAddress();
        if (Strings.isNullOrEmpty(mac)) {
            Log.e(TAG, "Failed to retrieve Wifi adapter MAC address");
            sendLocalBroadcast(new Intent(GcmDiscoveryMessages.WS_DEREGISTRATION_FAILED));
            return;
        }

        final String requestBody = new Gson().toJson(new DiscoveryUnregisterRequest(mac));

        GsonRequest<WsResponse> req = new GsonRequest<>(Request.Method.POST,
                getString(R.string.unregister_api), WsResponse.class, requestBody,
                new Response.Listener<WsResponse>() {
                    @Override
                    public void onResponse(WsResponse response) {
                        Log.d(TAG, "WS unsubscription finished=" + response.getStatus());

                        if (response.getStatus() == WsResponse.Status.success) {
                            sendLocalBroadcast(new Intent(GcmDiscoveryMessages.WS_DEREGISTRATION_SUCCEEDED));
                        } else {
                            sendLocalBroadcast(new Intent(GcmDiscoveryMessages.WS_DEREGISTRATION_FAILED));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "WS deregistration failed=" + error.getCause());
                sendLocalBroadcast(new Intent(GcmDiscoveryMessages.WS_DEREGISTRATION_FAILED));
            }
        });

        requestQueue.add(req);
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
