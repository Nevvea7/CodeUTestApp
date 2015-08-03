package app.nevvea.nomnom.data;

/**
 * Created by Anna on 8/3/15.
 */
public class SearchResult {
    private String restName;
    private String address;

    public SearchResult(String name, String address) {
        this.restName = name;
        this.address = address;
    }


    public String getAddress() {
        return address;
    }

    public String getRestName() {
        return restName;
    }
}
