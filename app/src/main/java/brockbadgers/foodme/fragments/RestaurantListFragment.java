package brockbadgers.foodme.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yelp.clientlib.entities.Business;

import java.util.ArrayList;

import brockbadgers.foodme.Activities.MainActivity;
import brockbadgers.foodme.R;
import brockbadgers.foodme.adapters.RestaurantListAdapter;


public class RestaurantListFragment extends Fragment {

    private TextView notFound;
    private LinearLayout results;
    private ListView searchList;
    private TextView nameSort;
    private TextView addressSort;
    private TextView ratingSort;
    private ArrayList<Business> businesses;

    private OnFragmentInteractionListener mListener;

    public RestaurantListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        results = (LinearLayout) v.findViewById(R.id.results);
        searchList = (ListView) v.findViewById(R.id.restaurant_listview);
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ((MainActivity)getActivity()).ViewRestaurant(businesses.get(position));
            }
        });
        results.setVisibility(View.GONE);
        notFound = (TextView) v.findViewById(R.id.no_items_matched);
        notFound.setVisibility(View.VISIBLE);

        nameSort = (TextView) v.findViewById(R.id.name_sort);
        nameSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).sortRestaurants(2);
            }
        });

        addressSort = (TextView) v.findViewById(R.id.address_sort);
        addressSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).sortRestaurants(0);
            }
        });

        ratingSort = (TextView) v.findViewById(R.id.rating_sort);
        ratingSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).sortRestaurants(1);
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    //TODO: add onclick for the sort buttons
    public void UpdateRestaurants(ArrayList<Business> restaurants)
    {
        //Load the search list
        if(restaurants.isEmpty() || (restaurants.size() == 1 && restaurants.get(0).id().equals(""))) {
            notFound.setVisibility(View.VISIBLE);
            results.setVisibility(View.GONE);
        }
        else {
            notFound.setVisibility(View.GONE);
            results.setVisibility(View.VISIBLE);
            businesses = restaurants;
            searchList.setAdapter(new RestaurantListAdapter(getActivity(), restaurants));
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
