package android.example.com.boguscode.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import androidx.annotation.Nullable;

/**
 * Custom wrapper around Volley's Request object to perform network operations.
 * @param <T> any {@link Request} object
 */
public class CustomObjectRequest<T> extends Request<T> {

    private static final String TAG = CustomObjectRequest.class.getSimpleName();

    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private boolean deliveredResponse;

    public CustomObjectRequest(final int method, final String url, @Nullable final Map<String, String> headers, @Nullable final Response.Listener<T> listener, @Nullable final Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.headers = headers;
        this.listener = listener;
        deliveredResponse = false;
        final DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(2500, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        setRetryPolicy(retryPolicy);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Response parseNetworkResponse(final NetworkResponse networkResponse) {
        if(networkResponse.statusCode == 200) {
            try {
                String contentType;
                if(networkResponse.headers.containsKey("Content-Type")) {
                    contentType = networkResponse.headers.get("Content-Type");
                    if(contentType != null && !contentType.contains("image")) {
                        String jsonResponse = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
                        return Response.success(new JSONObject(jsonResponse), HttpHeaderParser.parseCacheHeaders(networkResponse));
                    } else {
                        Bitmap bitmapResponse = getBitmapFromByteArray(networkResponse.data);
                        return Response.success(bitmapResponse, HttpHeaderParser.parseCacheHeaders(networkResponse));
                    }
                }
            }
            catch(UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            if(networkResponse.data != null) {
                try {
                    Log.e(TAG, "error code : " + networkResponse.statusCode);
                    final String networkResponseData = new String(networkResponse.data, Charset.defaultCharset());
                    if(TextUtils.isEmpty(networkResponseData)) {
                        Log.e(TAG, "Network response data is empty.");
                        return null;
                    }
                    final JsonElement jsonElement = new JsonParser().parse(networkResponseData);
                    if(!jsonElement.isJsonNull()) {
                        final JsonObject networkErrorObject = jsonElement.getAsJsonObject();
                        Log.e(TAG, networkErrorObject.entrySet().toString());
                    }
                }
                catch(JsonParseException exception) {
                    Log.e(TAG, "Could not parse network response data.");
                }
            }
        }
        return null;
    }

    @Override
    protected void deliverResponse(final T response) {
        if(listener!=null && !deliveredResponse) {
            deliveredResponse = true;
            listener.onResponse(response);
            cancel();
        }
    }

    private Bitmap getBitmapFromByteArray(byte[] imageByteArray) {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }
}
