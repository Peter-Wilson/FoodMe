package brockbadgers.foodme.Activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import brockbadgers.foodme.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //change the action bar to a search bar using defined layout
        ActionBar searchBar = getSupportActionBar();
        searchBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        searchBar.setDisplayShowHomeEnabled(false);
        searchBar.setDisplayShowTitleEnabled(false);
        LayoutInflater searchInflater = LayoutInflater.from(this);

        View searchView = searchInflater.inflate(R.layout.search_actionbar, null);
        getWindow().setStatusBarColor(getResources().getColor(R.color.logored));

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
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
