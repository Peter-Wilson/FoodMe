package brockbadgers.foodme.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import brockbadgers.foodme.R;
import brockbadgers.foodme.Fragments.RestaurantListFragment;
import brockbadgers.foodme.javaClasses.RestaurantComparator;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends ActionBarActivity implements OnMapReadyCallback, RestaurantListFragment.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private final int MY_PERMISSIONS_LOCATION = 0;
    //default location to 155 wellington if location permissions are not granted
    private LatLng location = new LatLng(43.6458088, -79.3879955);
    private ActionBar searchBar = null;
    private ArrayList<Business> businesses;
    private RestaurantListFragment listFragment;
    private Context c = null;
    static final String STATE_BUSINESS = "Business";
    public View loadingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load the list from the session
        listFragment = (RestaurantListFragment) getSupportFragmentManager().findFragmentById(R.id.restaurant_list);
        loadSession(savedInstanceState);
        c = this;

        //style the search var
        getWindow().setStatusBarColor(getResources().getColor(R.color.logored));
        initializeSearchBar();

        loadingScreen = findViewById(R.id.loadingPanel);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void loadSession(Bundle savedInstanceState) {
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null && savedInstanceState.getSerializable(STATE_BUSINESS) != null) {
            // Restore value of members from saved state
            businesses = (ArrayList<Business>)savedInstanceState.getSerializable(STATE_BUSINESS);
            listFragment.UpdateRestaurants(businesses);
        } else {
            businesses = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(STATE_BUSINESS, businesses);
        super.onSaveInstanceState(savedInstanceState);
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

        //TODO:add a loading screen when searching
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
                //Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                loadingScreen.setVisibility(View.VISIBLE);
                SearchRestaurant(query);
                return true;

            }

        });

        searchBar.setCustomView(searchView);
        searchBar.setDisplayShowCustomEnabled(true);
    }

    public void sortRestaurants(RestaurantComparator.Order sortType)
    {
        if(businesses != null) {
            RestaurantComparator comparator = new RestaurantComparator(sortType);
            Collections.sort(businesses, comparator); // now we have a sorted list
            AddMarkers(businesses);
            listFragment.UpdateRestaurants(businesses);
        }
    }

    private void SearchRestaurant(String query) {
        //do search
        try {
            YelpAPIFactory apiFactory = new YelpAPIFactory(getString(R.string.consumerKey),
                    getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
            YelpAPI yelpAPI = apiFactory.createAPI();

            //parameters
            Map<String, String> params = new HashMap<>();

            // general params
                        params.put("term", query);
                        params.put("limit", "10");

            // coordinates
            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(location.latitude)
                    .longitude(location.longitude).build();

            Call<SearchResponse> call = yelpAPI.search(coordinate, params);

            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
                    SearchResponse searchResponse = response.body();
                    Log.i("network errors", response.body().toString());
                    businesses = searchResponse.businesses();
                    listFragment.UpdateRestaurants(businesses);
                    loadingScreen.setVisibility(View.GONE);
                    AddMarkers(businesses);
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    // HTTP error happened, do something to handle it.
                    Log.i("network errors", t.getStackTrace().toString());
                    loadingScreen.setVisibility(View.GONE);
                }
            };

            call.enqueue(callback);

        }
        catch(Exception e){
            Log.i("network errors", e.getStackTrace().toString());
        }

    }

    public void AddMarkers(ArrayList<Business> businesses)
    {
        mMap.clear();
        for(int i = 0; i < businesses.size(); i++)
        {
            MarkerOptions m = new MarkerOptions().position(new LatLng(businesses.get(i).location().coordinate().latitude(),
                    businesses.get(i).location().coordinate().longitude()))
                    .title(""+(i+1));
            mMap.addMarker(m);
        }
    }

    public void ViewRestaurant(Business business)
    {
        Intent intent = new Intent(this, RestaurantInfoActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("Business", business);
        intent.putExtra("new", b);
        startActivity(intent);
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
        if(businesses != null)
            AddMarkers(businesses);
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
