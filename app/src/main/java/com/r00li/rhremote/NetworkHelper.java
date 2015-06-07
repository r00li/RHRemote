package com.r00li.rhremote;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by roli on 16/05/15.
 */
public class NetworkHelper {
    private static NetworkHelper mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private NetworkHelper(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkHelper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        // We need to increase the default timeout because blind requests take longer
        req.setTag("RHomeRequest");
        req.setRetryPolicy(new DefaultRetryPolicy(30*1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getRequestQueue().add(req);
    }

}
