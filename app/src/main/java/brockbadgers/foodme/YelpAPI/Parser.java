package brockbadgers.foodme.YelpAPI;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class Parser {
    /** ---------------------  Search TAG --------------------- */
    private static final String KEY_ROOT="businesses";
    private static final String KEY_ID="id";
    private static final String KEY_NAME="name";
    private static final String KEY_IMAGE_URL="image_url";
    private static final String KEY_URL="url";
    private static final String KEY_DISPLAY_PHONE="display_phone";
    private static final String KEY_REVIEW_COUNT="review_count";
    private static final String KEY_RATING="rating";
    private static final String KEY_RATING_URL_IMAGE="rating_img_url";
    private static final String KEY_SNIPPET_TEXT="snippet_text";
    private static final String KEY_ROOT_LOCATION="location";
    private static final String KEY_DISPLAY_ADDRESS="display_address";
    private static final String KEY_RESERVATION_URL="reservation_url";
    private static final String KEY_ROOT_COORDINATE="coordinate";
    private static final String KEY_LATITUDE="latitude";
    private static final String KEY_LONGITUDE="longitude";

    public JSONArray getResponseList(String searchResponse)
    {
        Log.i("response", "" + searchResponse);
        JSONArray items = null;
        if (searchResponse != null) {
            try {
                JSONObject json = new JSONObject(searchResponse);
                items = json.getJSONArray(KEY_ROOT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    public Restaurant getRestaurant(JSONArray list, int position){
        Restaurant object=new Restaurant();
        try{
            JSONObject r = list.getJSONObject(position);
            object.setId(r.get(KEY_ID).toString());
            object.setName(r.get(KEY_NAME).toString());
            object.setImageUrl(r.get(KEY_IMAGE_URL).toString());
            object.setUrl(r.get(KEY_URL).toString());
            object.setDisplayPhone(r.get(KEY_DISPLAY_PHONE).toString());
            object.setReviewCount((int) r.get(KEY_REVIEW_COUNT));
            object.setRating((double) r.get(KEY_RATING));
            object.setRatingImgUrl(r.get(KEY_RATING_URL_IMAGE).toString());
            object.setSnippetText(r.get(KEY_SNIPPET_TEXT).toString());
            StringBuilder sb = new StringBuilder();
            JSONObject location = r.getJSONObject(KEY_ROOT_LOCATION);
            JSONArray address = location.getJSONArray(KEY_DISPLAY_ADDRESS);
            for (int i = 0; i < address.length(); i++) {
                sb.append(address.getJSONObject(i));
            }
            object.setDisplayAddress(sb.toString());
            object.setReservationUrl(r.get(KEY_RESERVATION_URL).toString());
            JSONObject coordinate = location.getJSONObject(KEY_ROOT_COORDINATE);
            object.setLongitude((double)coordinate.get(KEY_LONGITUDE));
            object.setLongitude((double)coordinate.get(KEY_LATITUDE));

        }
        catch(Exception e)
        {
            Log.i("stacktrace", e.getStackTrace().toString());
        }
        return object;
    }
}
