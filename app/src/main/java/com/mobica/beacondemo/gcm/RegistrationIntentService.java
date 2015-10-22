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
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.mobica.beacondemo.R;
import com.mobica.beacondemo.model.MacRegisterRequest;
import com.mobica.beacondemo.model.MacRegisterResponse;
import com.mobica.beacondemo.volley.GsonRequest;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = RegistrationIntentService.class.getSimpleName();


    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // [START get_token]
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            TokenStore.updateToken(this);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + TokenStore.token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(TokenStore.token, sharedPreferences);
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(GcmPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, final SharedPreferences sharedPreferences) {
        // Add custom implementation, as needed.
        final String requestBody = new Gson().toJson(new MacRegisterRequest("f8:a9:d0:18:d2:d2", token));

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

        Volley.newRequestQueue(this).add(req);
    }
}
