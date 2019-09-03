package android.example.com.boguscode.dataModels;

/**
 * Data model for Pagination details.
 */
public class PaginationDetails {

    private int total;
    private int page;
    private int per_page;
    private Paging paging;

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getPerPage() {
        return per_page;
    }

    public Paging getPaging() {
        return paging;
    }

    /**
     * Data model for current page information.
     */
    public class Paging {
        private String next;
        private String previous;
        private String first;
        private String last;

        public String getNext() {
            return next;
        }

        public String getPrevious() {
            return previous;
        }

        public String getFirst() {
            return first;
        }

        public String getLast() {
            return last;
        }
    }

}
