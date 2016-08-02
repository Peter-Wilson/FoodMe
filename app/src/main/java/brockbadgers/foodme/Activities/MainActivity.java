package brockbadgers.foodme.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;

import brockbadgers.foodme.YelpAPI.Restaurant;
import brockbadgers.foodme.R;
import brockbadgers.foodme.YelpAPI.Parser;
import brockbadgers.foodme.YelpAPI.SignedRequestsHelper;
import brockbadgers.foodme.YelpAPI.UrlParameterHandler;
import brockbadgers.foodme.Fragments.RestaurantListFragment;
import brockbadgers.foodme.javaClasses.RestaurantComparator;

public class MainActivity extends ActionBarActivity implements OnMapReadyCallback, RestaurantListFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private final int MY_PERMISSIONS_LOCATION = 0;
    //default location to 155 wellington if location permissions are not granted
    private LatLng location = new LatLng(43.6458088, -79.3879955);
    private ActionBar searchBar = null;
    private ArrayList<Restaurant> restaurants;
    private RestaurantListFragment listFragment;
    private Context c = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c = this;
        getWindow().setStatusBarColor(getResources().getColor(R.color.logored));
        initializeSearchBar();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listFragment = (RestaurantListFragment) getSupportFragmentManager().findFragmentById(R.id.restaurant_list);
    }

    @NonNull
    private void initializeSearchBar() {
        //change the action bar to a search bar using defined layout
        searchBar = getSupportActionBar();
        searchBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        searchBar.setDisplayShowHomeEnabled(false);
        searchBar.setDisplayShowTitleEnabled(false);
        LayoutInflater searchInflater = LayoutInflater.from(this);

        View searchView = searchInflater.inflate(R.layout.search_actionbar, null);

        final SearchView search = (SearchView) searchView.findViewById(R.id.searchBar);

        //implement the search functionality in the action bar
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                //we arent searching on each keystroke so we wont be
                //doing anything in this function's implementation
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //we will query the Yelp API asychronously
                //while it is searching we can pop up a loading screen
                //TODO: search the YELP API

                //Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                SearchRestaurant(query);
                return true;

            }

        });

        searchBar.setCustomView(searchView);
        searchBar.setDisplayShowCustomEnabled(true);
    }

    public void sortRestaurants(int sortType)
    {
        RestaurantComparator.Order sortingBy = RestaurantComparator.Order.Name;

        switch(sortType){
            case 0:
                sortingBy = RestaurantComparator.Order.Address;
            break;
            case 1:
                sortingBy = RestaurantComparator.Order.Rating;
                break;
            default:
                break;
        }

        RestaurantComparator comparator = new RestaurantComparator();
        comparator.setSortingBy(sortingBy);
        Collections.sort(restaurants, comparator); // now we have a sorted list

    }

    private void SearchRestaurant(String query) {
        //do search
        try {
            UrlParameterHandler handler = UrlParameterHandler.getInstance();
            SignedRequestsHelper helper = new SignedRequestsHelper();

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = helper.sign(handler.buildMapForItemSearch(query.toString(), location.longitude, location.latitude));

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //initialize the list and parser
                            if(restaurants == null)
                                restaurants = new ArrayList<>();
                            Parser parser = new Parser();
                            JSONArray list = parser.getResponseList(response);

                            if(list == null) return;

                            //convert the response to a list
                            for(int i = 0; i < list.length(); i++)
                                restaurants.add(restaurants.size(), parser.getRestaurant(list, i));

                            listFragment.UpdateRestaurants(restaurants);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    error.printStackTrace();
                    Toast.makeText(c, "Error occurred: "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {
        //allow for interactions
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Loads the map when ready
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //check if the permissions are approved
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATION);
        }
        else {
            initializeLocation();
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
    }

    private void initializeLocation() {
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                location = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeLocation();
                }
                else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                }
                return;
            }
        }
    }
}
