package brockbadgers.foodme.javaClasses;

import com.yelp.clientlib.entities.Business;

import java.util.Comparator;


/**
 * Created by Peter on 8/2/2016.
 */
public class RestaurantComparator implements Comparator<Business> {
    public enum Order {Name, Address, Rating}

    private Order sortingBy = Order.Name;

    @Override
    public int compare(Business restaurant1, Business restaurant2) {
        switch(sortingBy) {
            case Name: return restaurant1.name().compareTo(restaurant2.name());
            case Address: return restaurant1.location().address().get(0).compareTo(restaurant2.location().address().get(0));
            case Rating: return ((Double)restaurant1.rating()).compareTo((Double)restaurant2.rating());
        }
        throw new RuntimeException("Practically unreachable code, can't be thrown");
    }

    public void setSortingBy(Order sortBy) {
        this.sortingBy = sortingBy;
    }
}
