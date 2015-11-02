package com.mobica.beacondemo.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.mobica.beacondemo.R;
import com.mobica.beacondemo.model.MacRegisterRequest;
import com.mobica.beacondemo.model.MacRegisterResponse;
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

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            TokenStore.updateToken(this);
            Log.i(TAG, "GCM Registration Token: " + TokenStore.token);

            if (TokenStore.token != null) {
                sendRegistrationToServer(TokenStore.token, sharedPreferences);
            } else {
                Log.e(TAG, "Failed to retrieve GCM token");
                sharedPreferences.edit().putBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, false).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(GcmPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Send Gcm token to our service
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, final SharedPreferences sharedPreferences) {
        final String mac = HwUtils.getWifiMacAddress();
        if (Strings.isNullOrEmpty(mac)) {
            Log.e(TAG, "Failed to retrieve Wifi adapter MAC address");
            return;
        }

        final String requestBody = new Gson().toJson(new MacRegisterRequest(mac, token));

        GsonRequest<MacRegisterResponse> req = new GsonRequest<>(Request.Method.POST,
                getString(R.string.ws_address), MacRegisterResponse.class, requestBody,
                new Response.Listener<MacRegisterResponse>() {
                    @Override
                    public void onResponse(MacRegisterResponse response) {
                        Log.d(TAG, "WS subscription finished=" + response.getStatus());

                        if (response.getStatus() == MacRegisterResponse.Status.success) {
                            sharedPreferences.edit().putBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                            LocalBroadcastManager.getInstance(RegistrationIntentService.this)
                                    .sendBroadcast(new Intent(GcmPreferences.WS_SUBSCRIPTION_SUCCESS));
                        } else {
                            LocalBroadcastManager.getInstance(RegistrationIntentService.this)
                                    .sendBroadcast(new Intent(GcmPreferences.WS_SUBSCRIPTION_FAILED));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "WS subscription failed=" + error.getCause());
                LocalBroadcastManager.getInstance(RegistrationIntentService.this)
                        .sendBroadcast(new Intent(GcmPreferences.WS_SUBSCRIPTION_FAILED));
            }
        });

        VolleyScheduler.enqueue(req);
    }
}
