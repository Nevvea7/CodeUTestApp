package app.nevvea.nomnom.data;

/**
 * Created by Anna on 8/3/15.
 */
public class SearchResult {
    private String restName;
    private String address;
    private String restID;

    public SearchResult(String name, String address, String restID) {
        this.restName = name;
        this.address = address;
        this.restID = restID;
    }


    public String getAddress() {
        return address;
    }

    public String getRestName() {
        return restName;
    }
}
