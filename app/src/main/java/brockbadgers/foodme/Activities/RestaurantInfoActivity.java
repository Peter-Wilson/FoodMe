package brockbadgers.foodme.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import brockbadgers.foodme.R;
import retrofit2.Call;
import retrofit2.Callback;

public class RestaurantInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Business business;
    ImageLoader imageLoader;
    GoogleMap mMap;
    static String STATE_RESTAUTANT = "NEW_RESTAURANT";
    TextView review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restauraunt_info);

        imageLoader = ImageLoader.getInstance();
        if (this != null) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }

        Intent intent = getIntent();
        if(intent != null) {
            business = (Business)(intent.getBundleExtra("new").getSerializable("Business"));
            fillFields();

        }
        else  if (savedInstanceState != null && savedInstanceState.getSerializable(STATE_RESTAUTANT) != null) {
                // Restore value of members from saved state
                business = (Business)savedInstanceState.getSerializable(STATE_RESTAUTANT);
                fillFields();
        }
        else{
            Toast.makeText(this, "Business Not Found", Toast.LENGTH_SHORT);
            finishActivity(0);
        }

        getWindow().setStatusBarColor(getResources().getColor(R.color.logored));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_restaurant);
        mapFragment.getMapAsync(this);
        review = (TextView)this.findViewById(R.id.review);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(business.name());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        YelpAPIFactory apiFactory = new YelpAPIFactory(getString(R.string.consumerKey),
                getString(R.string.consumerSecret), getString(R.string.token), getString(R.string.tokenSecret));
        YelpAPI yelpAPI = apiFactory.createAPI();

        Call<Business> call = yelpAPI.getBusiness(business.id());

        Callback<Business> callback = new Callback<Business>() {
            @Override
            public void onResponse(Call<Business> call, retrofit2.Response<Business> response) {
                Business b = response.body();
                Log.i("network errors", response.body().toString());
                business = b;

                if(business.reviews().size() > 0) {
                    review.setText(business.reviews().get(0).excerpt() + " - " + business.reviews().get(0).user().name());
                }
            }

            @Override
            public void onFailure(Call<Business> call, Throwable t) {
                // HTTP error happened, do something to handle it.
                Log.i("network errors", t.getStackTrace().toString());
            }
        };

        call.enqueue(callback);
    }

    private void fillFields() {
        //set address
        TextView address = (TextView)this.findViewById(R.id.address);
        StringBuilder sb = new StringBuilder();
        for(String s : business.location().address())
        {
            sb.append(s);
        }
        address.setText(sb.toString());

        //setDescription
        TextView description = (TextView)this.findViewById(R.id.description);
        description.setText(business.snippetText());

        //setReviewImage
        ImageView rating = (ImageView)this.findViewById(R.id.ratings_image);
        if(business.ratingImgUrlLarge() != "") {
            imageLoader.displayImage(business.ratingImgUrlLarge(), rating);
        }
        else {
            rating.setImageResource(R.drawable.ic_img_not_found);
        }

        //TODO: get a higher quality image source
        //setRestaurantImage
        ImageView restImage = (ImageView)this.findViewById(R.id.restaurant_image);
        if(business.imageUrl() != "") {
            imageLoader.displayImage(business.imageUrl(), restImage);
        }
        else {
            restImage.setImageResource(R.drawable.ic_img_not_found);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(STATE_RESTAUTANT, business);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Loads the map when ready
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng location = new LatLng(business.location().coordinate().latitude(),
                business.location().coordinate().longitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        mMap.addMarker(new MarkerOptions().position(location));
    }

}
