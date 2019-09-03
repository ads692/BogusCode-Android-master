package android.example.com.boguscode.listeners;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This class will receive messages when we have scrolled to the end
 * of a page in the RecyclerView. This will help us determine when to
 * request data for the next page.
 */
public abstract class NewPageRequestListener extends RecyclerView.OnScrollListener {

    private static final float MINIMUM_SCROLL_DISTANCE = 25;
    private int scrollDist = 0;
    private boolean isVisible = true;

    @NonNull
    private LinearLayoutManager layoutManager;

    protected NewPageRequestListener(@NonNull LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        // This is to determine when a request for the next page's data needs to be made.
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                loadNextPageData();
            }
        }

        // This is to determine when to hide and show the text view, which is present above the recycler view.
        if(isVisible && scrollDist > MINIMUM_SCROLL_DISTANCE && firstVisibleItemPosition > 5) {
            hideView();
            scrollDist = 0;
            isVisible = false;
        } else if (!isVisible && scrollDist < -MINIMUM_SCROLL_DISTANCE && firstVisibleItemPosition < 4) {
            showView();
            scrollDist = 0;
            isVisible = true;
        }
        if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
            scrollDist += dy;
        }
    }

    protected abstract void loadNextPageData();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

    public abstract void showView();

    public abstract void hideView();
}
