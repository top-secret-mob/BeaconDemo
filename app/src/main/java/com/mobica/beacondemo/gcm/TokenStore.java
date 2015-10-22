package com.mobica.beacondemo.gcm;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mobica.beacondemo.R;

import java.io.IOException;

/**
 * Created by wojtek on 22.10.15.
 */
public class TokenStore {

    public static String token;

    public static void updateToken(Context context) {
        try {
            token = InstanceID.getInstance(context).getToken(context.getString(R.string.project_id),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
