package com.example.bnayagrawal.popularmoviesstage2.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by bnayagrawal on 2/23/2018.
 */

public class VolleyNetworkHandler {
    private static VolleyNetworkHandler mVolleyNetworkHandler;
    private RequestQueue mRequestQueue;
    private JsonObjectRequest mJsonObjectRequest;
    private static Context mContext;

    private VolleyNetworkHandler(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyNetworkHandler getInstance(Context context) {
        if (mVolleyNetworkHandler == null) {
            mVolleyNetworkHandler = new VolleyNetworkHandler(context);
        }
        return mVolleyNetworkHandler;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public<T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public JsonObjectRequest getJsonObjectRequest() {
        return mJsonObjectRequest;
    }
}
