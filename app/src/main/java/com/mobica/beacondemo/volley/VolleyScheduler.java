package com.mobica.beacondemo.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.common.base.Preconditions;

/**
 * Created by woos on 2015-11-02.
 */
public class VolleyScheduler {
    private static RequestQueue queue;

    public static void init(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
    }

    public static <T> Request<T> enqueue(Request<T> request) {
        Preconditions.checkNotNull(queue, "Call init first");

        return queue.add(request);
    }
}
