package com.dtavana.foodswipe.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Restaurant")
public class Restaurant extends ParseObject {

    public static final String TAG = "Restaurant";

    public static final String KEY_VISITEDCOUNT = "visitedCount";
    public static final String KEY_NAME = "name";
    public static final String KEY_RESTAURANTID = "restaurantId";

    Number visitedCount = 0;
    String name;
    Number restaurantId;

    String address;
    String city;
    String phone;
    String photosUrl;
    String cuisines;
    String timings;
    List<String> highlights;
    Rating rating;
    int price;

    public static Restaurant fromJson(JSONObject obj) throws JSONException {
//        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
//        String restaurantId = obj.getString("id");
//        query.whereEqualTo("restaurantId", restaurantId);
//        Restaurant restaurant = query.getFirst();
        Restaurant restaurant = new Restaurant();

        JSONObject R = obj.getJSONObject("R");
        restaurant.setRestaurantId(R.getInt("res_id"));

        restaurant.setupVisitedCount();

        restaurant.setName(obj.getString("name"));

        JSONObject location = obj.getJSONObject("location");
        restaurant.setAddress(location.getString("address"));
        restaurant.setCity(location.getString("city"));

        JSONObject ratingRaw = obj.getJSONObject("user_rating");
        Rating rating = new Rating(
                ratingRaw.getDouble("aggregate_rating"),
                ratingRaw.getString("rating_text"),
                ratingRaw.getString("rating_color"),
                ratingRaw.getInt("votes"));
        restaurant.setRating(rating);

        restaurant.setPhone(obj.getString("phone_numbers"));
        restaurant.setPrice(obj.getInt("price_range"));
        restaurant.setCuisines(obj.getString("cuisines"));
        restaurant.setTimings(obj.getString("timings"));
        restaurant.setPhotosUrl(obj.getString("photos_url"));

        List<String> highlights = new ArrayList<>();
        JSONArray highlightsRaw = obj.getJSONArray("highlights");
        for(int i = 0; i < highlightsRaw.length(); i++) {
            highlights.add(highlightsRaw.getString(i));
        }
        restaurant.setHighlights(highlights);

        return restaurant;
    }

    private void setupVisitedCount() {
        ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
        query.whereEqualTo(KEY_RESTAURANTID, getRestaurantId());
        query.findInBackground(new FindCallback<Restaurant>() {
            @Override
            public void done(List<Restaurant> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "done: Error finding existing visited count", e);
                    setVisitedCount(0);
                    return;
                }
                if(objects != null && objects.size() > 0) {
                    Log.d(TAG, "done: Found existing restaurant");
                    setVisitedCount(objects.get(0).getNumber(KEY_VISITEDCOUNT));
                }
                else {
                    setVisitedCount(0);
                }
            }
        });
    }

    public Number getVisitedCount() {
        return visitedCount;
    }
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

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getPhotosUrl() {
        return photosUrl;
    }

    public void setPhotosUrl(String photosUrl) {
        this.photosUrl = photosUrl;
    }

    public String getCuisines() {
        return cuisines;
    }

    public void setCuisines(String cuisines) {
        this.cuisines = cuisines;
    }

    public String getTimings() {
        return timings;
    }

    public void setTimings(String timings) {
        this.timings = timings;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public static class Rating {
        public double rating;
        public String text;
        public String color;
        public int votes;

        public Rating(double rating, String text, String color, int votes) {
            this.rating = rating;
            this.text = text;
            this.color = color;
            this.votes = votes;
        }

        public String getVotesString() {
            return String.format("%s : %d Votes", text, votes);
        }

        public String getRatingString() {
            return String.format("%.2f/5.0", rating);
        }
    }
}
