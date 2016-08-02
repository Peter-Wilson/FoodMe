package brockbadgers.foodme.YelpAPI;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Calendar;
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

    private String timestamp() {
        String timestamp = null;
        Calendar cal = Calendar.getInstance();
        timestamp = ""+cal.getTime().getTime();
        return timestamp;
    }

    // Specify filter parameters
    public Map<String,String> buildMapForItemSearch(String productName, double longitude, double latitude){
        SecureRandom random = new SecureRandom();
        Map<String, String> myparams = new HashMap<String, String>();
        myparams.put("term", productName);
        myparams.put("ll", latitude+","+longitude);
        myparams.put("oauth_timestamp", timestamp());
        myparams.put("oauth_nonce", new BigInteger(130, random).toString(32));
        return myparams;
    }
}