package brockbadgers.foodme.javaClasses;

import com.yelp.clientlib.entities.Business;

import java.util.Comparator;


/**
 * Created by Peter on 8/2/2016.
 */
public class RestaurantComparator implements Comparator<Business> {
    public enum Order {Name, Address, Rating}
    public Order sortingBy;

    public RestaurantComparator(Order type)
    {
        sortingBy = type;
    }

    @Override
    public int compare(Business restaurant1, Business restaurant2) {
        switch(sortingBy) {
            case Name: return restaurant1.name().compareTo(restaurant2.name());
            case Address: return restaurant1.location().address().get(0).compareTo(restaurant2.location().address().get(0));
            case Rating: return ((Double)restaurant2.rating()).compareTo((Double)restaurant1.rating());
        }
        throw new RuntimeException("Practically unreachable code, can't be thrown");
    }
}
