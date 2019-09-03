package android.example.com.boguscode.adapters;

import android.content.Context;
import android.example.com.boguscode.R;
import android.example.com.boguscode.constants.AppConstants;
import android.example.com.boguscode.dataModels.ContentDetails;
import android.example.com.boguscode.dataModels.PicturePayload.PictureSizes;
import android.example.com.boguscode.dataModels.UserDetails;
import android.example.com.boguscode.utils.AppUtils;
import android.example.com.boguscode.utils.CustomObjectRequest;
import android.example.com.boguscode.utils.VolleySingleton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Custom Adapter for showcasing the content details for every item using the {@link RecyclerView}.
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ItemViewHolder> {

    private static final String CREATED_BY = "Created by: ";

    public interface ItemListenerInterface {
        void onClick(int position);
    }

    private ItemListenerInterface itemListenerInterface;
    private ArrayList<ContentDetails> contentDetailsList;
    private Context context;

    public ContentAdapter(ArrayList<ContentDetails> arrayList, ItemListenerInterface itemListenerInterface) {
        this.contentDetailsList = arrayList;
        this.itemListenerInterface = itemListenerInterface;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        context = parent.getContext().getApplicationContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_video, parent, false);
        return new ItemViewHolder(view, itemListenerInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        final ImageView videoThumbnail = holder.videoThumbnail;
        final TextView videoName = holder.videoName;
        final TextView videoCreator = holder.videoCreator;

        final ArrayList<PictureSizes> pictureSizes = contentDetailsList.get(position).getPictures().getSizes();
        final String thumbnailUrl = pictureSizes.get(pictureSizes.size()-1).getLink();
        final UserDetails userDetails = contentDetailsList.get(position).getUserDetails();

//        Image thumbnails can also be requested with Volley, as shown in the 2 lines below, by making use of
//        Volley's network request capabilities. However, I feel Glide is specifically created to excel in downloading
//        a large amount of images and customized to cache it, making it easier to use and create in just 1 line. As I
//        was limited to using just 1 external dependency, I've commented out Glide's implementation below.

        final CustomObjectRequest imageRequest = AppUtils.makeImageRequest(thumbnailUrl, videoThumbnail, context);
        imageRequest.setTag(AppConstants.IMAGE_REQUEST_TAG);
        VolleySingleton.getInstance(context).addToRequestQueue(imageRequest);

//        Glide.with(context).load(thumbnailUrl).diskCacheStrategy(DiskCacheStrategy.DATA).into(videoThumbnail);

        videoName.setText(contentDetailsList.get(position).getName());
        videoCreator.setText(CREATED_BY.concat(userDetails.getName()));
    }

    @Override
    public int getItemCount() {
        return contentDetailsList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView videoThumbnail;
        TextView videoName;
        TextView videoCreator;
        ItemListenerInterface itemListenerInterface;

        ItemViewHolder(@NonNull final View itemView, final ItemListenerInterface itemListenerInterface) {
            super(itemView);
            this.videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            this.videoName = itemView.findViewById(R.id.list_item_video_name);
            this.videoCreator = itemView.findViewById(R.id.list_item_video_creator);

            this.itemListenerInterface = itemListenerInterface;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            itemListenerInterface.onClick(getAdapterPosition());
        }
    }
}
