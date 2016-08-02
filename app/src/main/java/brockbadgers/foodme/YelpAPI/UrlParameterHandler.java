package brockbadgers.foodme.YelpAPI;

import java.util.HashMap;
import java.util.Map;

/*
    Helper class to convert filter list into query string for Yelp API call
*/
public class UrlParameterHandler {

    public static UrlParameterHandler paramHandler;
    private UrlParameterHandler() {}

    public static synchronized UrlParameterHandler getInstance(){
        if(paramHandler==null){
            paramHandler=new UrlParameterHandler();
            return paramHandler;
        }
        return paramHandler;
    }

    // Specify filter parameters
    public Map<String,String> buildMapForItemSearch(String productName, double longitude, double latitude){
        Map<String, String> myparams = new HashMap<String, String>();
        myparams.put("term", productName);
        myparams.put("ll", latitude+","+longitude);
        return myparams;
    }
}