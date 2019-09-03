package android.example.com.boguscode.dataModels;

/**
 * Data Model for parsing relevant content details from a page.
 */
public class ContentDetails {
    private String name;
    private String link;
    private PicturePayload pictures;
    private UserDetails user;

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public PicturePayload getPictures() {
        return pictures;
    }

    public UserDetails getUserDetails() {
        return user;
    }
}
