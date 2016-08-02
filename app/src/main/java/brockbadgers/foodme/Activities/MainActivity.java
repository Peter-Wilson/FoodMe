package brockbadgers.foodme.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import brockbadgers.foodme.R;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final int MY_PERMISSIONS_LOCATION = 0;
    //default location to 155 wellington if location permissions are not granted
    private LatLng location = new LatLng(43.6458088, -79.3879955);
    private ActionBar searchBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().setStatusBarColor(getResources().getColor(R.color.logored));
        initializeSearchBar();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                return true;

            }

        });

        searchBar.setCustomView(searchView);
        searchBar.setDisplayShowCustomEnabled(true);
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
