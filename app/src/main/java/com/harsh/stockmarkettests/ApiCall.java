package com.harsh.stockmarkettests;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ApiCall {

    private static ApiCall mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public ApiCall(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }
    public static synchronized ApiCall getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ApiCall(context);
        }
        return mInstance;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);
    }

    public static void make(Context ctx, String query, String url, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + query,
                listener, errorListener);
        ApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }


    //For Index
    public static void makeIndex(Context ctx, String url, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        ApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public static void makeTickerName(Context ctx, String url, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        ApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }

}
