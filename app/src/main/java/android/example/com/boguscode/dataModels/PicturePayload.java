package android.example.com.boguscode.dataModels;

import java.util.ArrayList;

/**
 * Data model for a picture's payload.
 */
public class PicturePayload {
    private ArrayList<PictureSizes> sizes;

    public ArrayList<PictureSizes> getSizes() {
        return sizes;
    }

    public class PictureSizes {
        private String link;

        public String getLink() {
            return link;
        }
    }
}
