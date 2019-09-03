package android.example.com.boguscode.utils;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Singleton class to maintain a request queue for all Volley operation in the app.
 */
public class VolleySingleton {

    private static VolleySingleton instance;
    private static RequestQueue requestQueue;
    private Context context;

    private VolleySingleton (Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if(instance == null) {
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        requestQueue.add(request);
    }
}
