package android.example.com.boguscode.utils;

import android.content.Context;
import android.example.com.boguscode.R;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

/**
 * Utility methods for the entire app.
 */
public class AppUtils {

    /**
     * Downloads an image using Volley and sets it in the provided image view.
     * @param imageUrl url for the image.
     * @param videoThumbnail {@link ImageView} to set the image in.
     * @return {@link CustomObjectRequest} object.
     */
    public static CustomObjectRequest makeImageRequest(final String imageUrl, final ImageView videoThumbnail, final Context context) {

        final Response.Listener listener = new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(final Bitmap image) {
                if(image != null) {
                    videoThumbnail.setImageBitmap(image);
                }
            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                Toast.makeText(context, "Something went wrong while downloading the image", Toast.LENGTH_SHORT).show();
                videoThumbnail.setImageResource(R.drawable.error);
            }
        };

        return new CustomObjectRequest<ImageRequest>(Request.Method.GET, imageUrl, null, listener, errorListener);
    }
}
