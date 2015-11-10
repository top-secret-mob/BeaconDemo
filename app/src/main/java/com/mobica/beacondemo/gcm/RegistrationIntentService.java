package com.mobica.beacondemo.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.mobica.beacondemo.R;
import com.mobica.beacondemo.model.MacRegisterRequest;
import com.mobica.beacondemo.model.MacUnregisterRequest;
import com.mobica.beacondemo.model.WsResponse;
import com.mobica.beacondemo.utils.HwUtils;
import com.mobica.beacondemo.volley.GsonRequest;
import com.mobica.beacondemo.volley.VolleyScheduler;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = RegistrationIntentService.class.getSimpleName();
    public static final String ACTION_REGISTER = "ACTION_REGISTER";
    public static final String ACTION_UNREGISTER = "ACTION_UNREGISTER";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();

        if (ACTION_REGISTER.equals(action)) {
            try {
                TokenStore.updateToken(this);
                Log.i(TAG, "GCM Registration Token: " + TokenStore.token);

                if (TokenStore.token != null) {
                    registerToServer(TokenStore.token);
                } else {
                    Log.e(TAG, "Failed to retrieve GCM token");
                    sendLocalBroadcast(new Intent(GcmMessages.WS_REGISTRATION_FAILED));
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to complete token refresh", e);
                sendLocalBroadcast(new Intent(GcmMessages.WS_REGISTRATION_FAILED));
            }
        } else if (ACTION_UNREGISTER.equals(action)) {
            unregisterFromServer();
        }
    }

    /**
     * Send Gcm token to our service
     *
     * @param token The new token.
     */
    private void registerToServer(String token) {
        final String mac = HwUtils.getWifiMacAddress();
        if (Strings.isNullOrEmpty(mac)) {
            Log.e(TAG, "Failed to retrieve Wifi adapter MAC address");
            sendLocalBroadcast(new Intent(GcmMessages.WS_REGISTRATION_FAILED));
            return;
        }

        final String requestBody = new Gson().toJson(new MacRegisterRequest(mac, token));

        GsonRequest<WsResponse> req = new GsonRequest<>(Request.Method.POST,
                getString(R.string.register_api), WsResponse.class, requestBody,
                new Response.Listener<WsResponse>() {
                    @Override
                    public void onResponse(WsResponse response) {
                        Log.d(TAG, "WS subscription finished=" + response.getStatus());

                        if (response.getStatus() == WsResponse.Status.success) {
                            sendLocalBroadcast(new Intent(GcmMessages.WS_REGISTRATION_SUCCEEDED));
                        } else {
                            sendLocalBroadcast(new Intent(GcmMessages.WS_REGISTRATION_FAILED));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "WS subscription failed=" + error.getCause());
                sendLocalBroadcast(new Intent(GcmMessages.WS_REGISTRATION_FAILED));
            }
        });

        VolleyScheduler.enqueue(req);
    }

    /**
     * Send message to web service in order to unregister
     */
    private void unregisterFromServer() {
        final String mac = HwUtils.getWifiMacAddress();
        if (Strings.isNullOrEmpty(mac)) {
            Log.e(TAG, "Failed to retrieve Wifi adapter MAC address");
            sendLocalBroadcast(new Intent(GcmMessages.WS_DEREGISTRATION_FAILED));
            return;
        }

        final String requestBody = new Gson().toJson(new MacUnregisterRequest(mac));

        GsonRequest<WsResponse> req = new GsonRequest<>(Request.Method.POST,
                getString(R.string.unregister_api), WsResponse.class, requestBody,
                new Response.Listener<WsResponse>() {
                    @Override
                    public void onResponse(WsResponse response) {
                        Log.d(TAG, "WS unsubscription finished=" + response.getStatus());

                        if (response.getStatus() == WsResponse.Status.success) {
                            sendLocalBroadcast(new Intent(GcmMessages.WS_DEREGISTRATION_SUCCEEDED));
                        } else {
                            sendLocalBroadcast(new Intent(GcmMessages.WS_DEREGISTRATION_FAILED));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "WS deregistration failed=" + error.getCause());
                sendLocalBroadcast(new Intent(GcmMessages.WS_DEREGISTRATION_FAILED));
            }
        });

        VolleyScheduler.enqueue(req);
    }

    private void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
