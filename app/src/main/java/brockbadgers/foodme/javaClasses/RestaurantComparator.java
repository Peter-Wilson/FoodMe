package brockbadgers.foodme.javaClasses;

import java.util.Comparator;

import brockbadgers.foodme.YelpAPI.Restaurant;

/**
 * Created by Peter on 8/2/2016.
 */
public class RestaurantComparator implements Comparator<Restaurant> {
    public enum Order {Name, Address, Rating}

    private Order sortingBy = Order.Name;

    @Override
    public int compare(Restaurant restaurant1, Restaurant restaurant2) {
        switch(sortingBy) {
            case Name: return restaurant1.getName().compareTo(restaurant2.getName());
            case Address: return restaurant1.getDisplayAddress().compareTo(restaurant2.getDisplayAddress());
            case Rating: return ((Double)restaurant1.getRating()).compareTo((Double)restaurant2.getRating());
        }
        throw new RuntimeException("Practically unreachable code, can't be thrown");
    }

    public void setSortingBy(Order sortBy) {
        this.sortingBy = sortingBy;
    }
}
