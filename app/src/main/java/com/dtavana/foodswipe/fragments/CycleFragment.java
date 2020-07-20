package com.dtavana.foodswipe.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.dtavana.foodswipe.databinding.FragmentCycleBinding;
import com.dtavana.foodswipe.models.Restaurant;
import com.dtavana.foodswipe.network.FoodSwipeApplication;
import com.dtavana.foodswipe.network.FoodSwipeClient;
import com.dtavana.foodswipe.utils.OnSwipeTouchListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class CycleFragment extends Fragment {

    public static final String TAG = "CycleFragment";

    public static final int PERMISSION_REQUEST_CODE = 999;

    FragmentCycleBinding binding;

    List<Restaurant> restaurants;
    List<Restaurant> accepted;
    List<Restaurant> denied;
    int currentRestaurant = 0;
    
    FoodSwipeClient client;

    public CycleFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        restaurants = new ArrayList<>();
        accepted = new ArrayList<>();
        denied = new ArrayList<>();
        client = FoodSwipeApplication.getClient();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        binding = FragmentCycleBinding.inflate(getLayoutInflater(), parent, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        loadRestaurants();
        binding.getRoot().setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                Log.d(TAG, "onSwipeLeft: Swiping left");
                denied.add(restaurants.get(currentRestaurant));
                currentRestaurant++;
                showCurrentRestaurant();
            }

            @Override
            public void onSwipeRight() {
                Log.d(TAG, "onSwipeRight: Swiping right");
                accepted.add(restaurants.get(currentRestaurant));
                currentRestaurant++;
                showCurrentRestaurant();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Can not proceed, user denied location permission");
                    // TODO: Show error message
                    return;
                }
            }
            Log.d(TAG, "onRequestPermissionsResult: Successfully granted permissions to use location");
            loadRestaurants();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("NewApi")
    private void loadRestaurants() {
        // TODO: Change to allow for configurable restaurants
        // TODO: Progress bar
        if(restaurants == null || restaurants.size() == 0) {
            Log.d(TAG, "loadRestaurants: Caching restaurants from API");
            final RequestParams params = new RequestParams();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "loadRestaurants: Requesting permissions for location");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
                return;
            }
            LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    params.put("lat", (long) location.getLatitude());
                    params.put("lon", (long) location.getLongitude());
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
            });
        }
        else {
            Log.d(TAG, "loadRestaurants: Restaurants were loaded, skipping API Call");
            showCurrentRestaurant();
        }

    }

    private void showCurrentRestaurant() {
        Log.d(TAG, "showCurrentRestaurant: Showing current restaurant");
        // TODO: Limit number of restaurants that can be visited
        if(currentRestaurant >= restaurants.size()) {
            Log.d(TAG, "showCurrentRestaurant: Finished iterating through restaurants");
            Log.d(TAG, "showCurrentRestaurant: accepted" + accepted.toString());
            binding.getRoot().setOnTouchListener(null);
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