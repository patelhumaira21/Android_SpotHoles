/**
 * File Name : VolleyQueue.java
 * Created by: Humaira Patel
 * Date: 15/03/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.content.Context;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * This class represents a singleton volley object for the network
 * communication.
 *
 */
public class VolleyQueue {

    private static VolleyQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    /**
     * Private constructor.
     *
     * @param context
     */
    private VolleyQueue(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * This method returns an instance of volley object.
     *
     * @param context
     * @return
     */
    public static synchronized VolleyQueue instance(Context context) {
        if ( mInstance == null ) {
            mInstance = new VolleyQueue(context);
        }
        return mInstance;
    }

    /**
     * This method returns volley's request queue.
     *
     * @return
     */
    public RequestQueue getRequestQueue() {
        if(mRequestQueue==null){
            Cache cache = new DiskBasedCache(mContext.getCacheDir(),2*1024*1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache,network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }


    /**
     * This method adds the given request in volley's request queue.
     *
     * @param req
     * @param <T>
     */
    public <T> void add(Request<T> req) {
        mRequestQueue.add(req);
    }

}
