package com.dtavana.foodswipe.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.dtavana.foodswipe.databinding.FragmentCycleBinding;
import com.dtavana.foodswipe.models.Restaurant;
import com.dtavana.foodswipe.network.FoodSwipeApplication;
import com.dtavana.foodswipe.network.FoodSwipeClient;
import com.dtavana.foodswipe.utils.GlideApp;
import com.dtavana.foodswipe.utils.OnSwipeTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class CycleFragment extends Fragment {

    public static final String TAG = "CycleFragment";

    FragmentCycleBinding binding;

    List<Restaurant> restaurants;
    FoodSwipeClient client;

    int currentRestaurant = 0;
    boolean restaurantsLoaded = false;

    public CycleFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        binding = FragmentCycleBinding.inflate(getLayoutInflater(), parent, false);
        client = FoodSwipeApplication.getClient();
        restaurants = new ArrayList<>();
        loadRestaurants();
        showCurrentRestaurant();
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.getRoot().setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                // TODO: Add to rejected / accepted lists
                Log.d(TAG, "onSwipeLeft: Swiping left");
                currentRestaurant++;
                showCurrentRestaurant();
            }
            @Override
            public void onSwipeRight() {
                // TODO: Add to rejected / accepted lists
                Log.d(TAG, "onSwipeRight: Swiping right");
                currentRestaurant++;
                showCurrentRestaurant();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        restaurants = null;
        client = null;
    }

    private void loadRestaurants() {
        // TODO: Change to allow for configurable restaurants
        // TODO: Progress bar
        // TODO: Get Latitude and Longitude from GPSProvider
        RequestParams params = new RequestParams();
        params.put("lat", 42);
        params.put("lon", -71);
        client.getRestaurants(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject data = json.jsonObject;
                Log.d(TAG, "onSuccess: Successfully loaded restaurants with response: " + data.toString());
                try {
                    int count = data.getInt("results_shown");
                    if(count > 0) {
                        JSONArray allRestaurants = data.getJSONArray("restaurants");
                        for(int i = 0; i < allRestaurants.length(); i++) {
                            JSONObject currentRestaurant = allRestaurants.getJSONObject(i).getJSONObject("restaurant");
                            Restaurant newRestaurant = Restaurant.fromJson(currentRestaurant);
                            restaurants.add(newRestaurant);
                        }
                        Log.d(TAG, "onSuccess: Successfully loaded " + count + " restaurants into cache");
                        restaurantsLoaded = true;
                        showCurrentRestaurant();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,
                        "onFailure: Failed to load restaurants with status code: " + statusCode + " and response: " + response,
                        throwable);
            }
        });
    }

    private void showCurrentRestaurant() {
        // TODO: Limit number of restaurants that can be visited
        if(currentRestaurant >= restaurants.size()) {
            // TODO Switch to ListFragment as we have gone through all restaurants
            return;
        }
        Restaurant restaurant = restaurants.get(currentRestaurant);
        binding.tvName.setText(restaurant.getName());
//        Log.d(TAG, "showCurrentRestaurant: ImageURL: " + restaurant.getPhotosUrl());
//        // TODO: getImageURL is now a website rather than an image URL
//        GlideApp
//                .with(this)
//                .load(restaurant.getImageUrl())
//                .into(binding.ivPicture);
    }
}