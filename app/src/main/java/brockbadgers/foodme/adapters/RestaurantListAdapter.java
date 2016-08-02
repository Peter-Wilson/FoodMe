package brockbadgers.foodme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yelp.clientlib.entities.Business;

import java.util.List;

import brockbadgers.foodme.R;

/**
 * Created by Peter on 8/2/2016.
 */
public class RestaurantListAdapter extends ArrayAdapter<Business> implements View.OnClickListener {

    private Context mContext;
    private List<Business> mList;
    ImageLoader imageLoader;

    public RestaurantListAdapter(Context context, List<Business> list) {
        super(context, R.layout.list_item_restaurant, list);
        mContext = context;
        imageLoader = ImageLoader.getInstance();
        if (context != null) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Business getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Necessary implementation
    @Override
    public void onClick(View v) {

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //if (view == null) {
        Business restaurant = getItem(position);
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.list_item_restaurant, parent, false);

        TextView number = (TextView)view.findViewById(R.id.restaurant_number);
        TextView name = (TextView)view.findViewById(R.id.restaurant_name);
        TextView address = (TextView)view.findViewById(R.id.restaurant_address);
        ImageView rating = (ImageView)view.findViewById(R.id.restaurant_rating);

        // Populate item's widgets
        number.setText(""+(position+1));
        name.setText(restaurant.name());
        StringBuilder sb = new StringBuilder();
        for(String s : restaurant.location().address())
        {
            sb.append(s);
        }
        address.setText(sb.toString());

        // Display returned image or a default
        if(restaurant.ratingImgUrlSmall() != "") {
            imageLoader.displayImage(restaurant.ratingImgUrlSmall(), rating);
        }
        else {
            rating.setImageResource(R.drawable.ic_img_not_found);
        }
        return view;
    }


}
