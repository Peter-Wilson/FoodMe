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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import brockbadgers.foodme.R;
import brockbadgers.foodme.YelpAPI.Restaurant;

/**
 * Created by Peter on 8/2/2016.
 */
public class RestaurantListAdapter extends ArrayAdapter<Restaurant> implements View.OnClickListener {

    private Context mContext;
    private List<Restaurant> mList;
    ImageLoader imageLoader;

    public RestaurantListAdapter(Context context, List<Restaurant> list) {
        super(context, R.layout.list_item_product, list);
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
    public Restaurant getItem(int pos) {
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
    // TODO: change this to the new view
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //if (view == null) {
        Restaurant product = getItem(position);
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = li.inflate(R.layout.list_item_product, parent, false);

        TextView productName = (TextView)view.findViewById(R.id.product_name);
        TextView productPrice = (TextView)view.findViewById(R.id.product_price);
        ImageView productIcon = (ImageView)view.findViewById(R.id.product_icon);

        // Populate item's widgets
        productName.setText(product.getTitle());
        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        productPrice.setText(currencyFormatter.format(product.getPrice()) + " from Amazon");

        // Display returned image or a default
        if(product.getImageUrl() != "") {
            imageLoader.displayImage(product.getImageUrl(), productIcon);
        }
        else {
            productIcon.setImageResource(R.drawable.ic_img_not_found);
        }
        //}
        return view;
    }


}
