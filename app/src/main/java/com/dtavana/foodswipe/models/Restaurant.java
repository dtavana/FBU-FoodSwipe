package com.dtavana.foodswipe.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {

    public static final String TAG = "Restaurant";

    public static final String KEY_VISITEDCOUNT = "visitedCount";
    public static final String KEY_NAME = "name";
    public static final String KEY_RESTAURANTID = "restaurantId";

    Number visitedCount;
    String name;
    Number restaurantId;

    String address;
    String city;
    String state;
    String zipCode;
    String phone;
    String reserveUrl;
    String imageUrl;
    int price;

    public static Restaurant fromJson(JSONObject obj) throws JSONException, ParseException {
//        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
//        String restaurantId = obj.getString("id");
//        query.whereEqualTo("restaurantId", restaurantId);
//        Restaurant restaurant = query.getFirst();
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(obj.getInt("id"));
        restaurant.setName(obj.getString("name"));
        restaurant.setAddress(obj.getString("address"));
        restaurant.setCity(obj.getString("city"));
        restaurant.setState(obj.getString("state"));
        restaurant.setZipCode(obj.getString("postal_code"));
        restaurant.setPhone(obj.getString("phone"));
        restaurant.setReserveUrl(obj.getString("mobile_reserve_url"));
        restaurant.setImageUrl(obj.getString("image_url"));
        restaurant.setPrice(obj.getInt("price"));
        return restaurant;
    }

    public Number getVisitedCount() { return visitedCount; }
    public void setVisitedCount(Number visitedCount) { put(KEY_VISITEDCOUNT, visitedCount); this.visitedCount = visitedCount; }

    public String getName() { return name; }
    public void setName(String name) { put(KEY_NAME, name); this.name = name; }

    public Number getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Number restaurantId) { put(KEY_RESTAURANTID, restaurantId); this.restaurantId = restaurantId; }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReserveUrl() {
        return reserveUrl;
    }
    public void setReserveUrl(String reserveUrl) {
        this.reserveUrl = reserveUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
}
