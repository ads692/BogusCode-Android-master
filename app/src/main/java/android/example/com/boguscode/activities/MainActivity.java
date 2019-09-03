package android.example.com.boguscode.activities;

import android.content.Intent;
import android.example.com.boguscode.adapters.ContentAdapter;
import android.example.com.boguscode.R;
import android.example.com.boguscode.constants.AppConstants;
import android.example.com.boguscode.dataModels.PaginationDetails;
import android.example.com.boguscode.listeners.NewPageRequestListener;
import android.example.com.boguscode.utils.CustomObjectRequest;
import android.example.com.boguscode.utils.VolleySingleton;
import android.example.com.boguscode.dataModels.ContentDetails;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static android.content.Intent.ACTION_VIEW;

/**
 * Main Activity for showing videos in a recycler view, emulating infinite scrolling.
 */
public class MainActivity extends AppCompatActivity implements ContentAdapter.ItemListenerInterface {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView textView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private boolean isLastPage = false;

    private static boolean isLoading = false;
    private static ArrayList<ContentDetails> contentDetailsList = new ArrayList<>();
    private static ContentAdapter contentAdapter;
    private static String nextPageRequestUrl;
    private static int currentPageNumber;
    private static LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final DividerItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());

        textView = findViewById(R.id.textView);
        textView.setVisibility(View.VISIBLE);

        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.addOnScrollListener(new NewPageRequestListener(layoutManager) {
            @Override
            protected void loadNextPageData() {
                isLoading = true;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getNextPageRequestUrl() == null) {
                            Toast.makeText(MainActivity.this, "No more videos available.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            loadPageData(AppConstants.DOMAIN + getNextPageRequestUrl());
                            isLoading = false;
                            handler.removeCallbacks(this);
                        }
                    }
                }, 1000);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public void showView() {
                textView.setVisibility(View.VISIBLE);
                textView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();

            }

            @Override
            public void hideView() {
                textView.animate().translationY(-textView.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
                textView.setVisibility(View.GONE);
            }
        });

        loadPageData(AppConstants.DOMAIN + AppConstants.REQUEST_URL);
    }

    /**
     * Implements the onClick functionality on every recycler view item.
     * Clicking on a item will either launch it in the browser or the Vimeo Android app, if installed.
     * @param position scroll position of the item.
     */
    @Override
    public void onClick(final int position) {
        final String videoUrl = contentDetailsList.get(position).getLink();
        Log.i(TAG, "Launching video - " + videoUrl);
        final Intent intent = new Intent(ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(videoUrl));
        startActivity(intent);
    }

    private static int getCurrentPageNumber() {
        return currentPageNumber;
    }

    private static String getNextPageRequestUrl() {
        return nextPageRequestUrl;
    }

    /**
     * This method makes a request for a page's data using Volley, asynchronously and sets the data on the adapter.
     * @param requestUrl The url for the requested page.
     */
    private void loadPageData(final String requestUrl) {
        progressBar.setVisibility(View.VISIBLE);

        final Map<String, String> headers = new HashMap<>();
        headers.put(AppConstants.AUTH_HEADER, AppConstants.AUTH_TOKEN);

        final Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                // Getting current and next page details
                final PaginationDetails paginationDetails = new Gson().fromJson(response.toString(), PaginationDetails.class);
                currentPageNumber = paginationDetails.getPage();
                nextPageRequestUrl = paginationDetails.getPaging().getNext();
                Log.i(TAG, "Fetching videos for page number " + getCurrentPageNumber());

                final JSONArray data;
                try {
                    // Parsing data for the current page
                    data = response.getJSONArray("data");
                    for(int i = 0; i < data.length(); i++) {
                        String jsonString = data.getJSONObject(i).toString();
                        ContentDetails contentDetails = new Gson().fromJson(jsonString, ContentDetails.class);
                        contentDetailsList.add(contentDetails);
                    }
                    if(contentDetailsList != null && !contentDetailsList.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Page " + getCurrentPageNumber() + " loaded.", Toast.LENGTH_SHORT).show();
                        if(contentAdapter == null) {
                            contentAdapter = new ContentAdapter(contentDetailsList, MainActivity.this);
                            recyclerView.setAdapter(contentAdapter);
                        }
                        else {
                            // Update recycler view with fresh content and also maintain scroll position
                            contentAdapter.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
                catch(JSONException e) {
                    Log.e(TAG, "Could not parse the data.");
                    e.printStackTrace();
                }

            }
        };

        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error occurred while fetching page data. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error occurred while fetching page data. " + error.toString());
            }
        };

        final CustomObjectRequest jsonObjectRequest = new CustomObjectRequest<JsonObjectRequest>(Request.Method.GET, requestUrl, headers, listener, errorListener);
        jsonObjectRequest.setTag(AppConstants.JSON_REQUEST_TAG);
        VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(VolleySingleton.getInstance(this).getRequestQueue() != null) {
            VolleySingleton.getInstance(this).getRequestQueue().cancelAll(AppConstants.JSON_REQUEST_TAG);
            VolleySingleton.getInstance(this).getRequestQueue().cancelAll(AppConstants.IMAGE_REQUEST_TAG);
        }
    }
}
