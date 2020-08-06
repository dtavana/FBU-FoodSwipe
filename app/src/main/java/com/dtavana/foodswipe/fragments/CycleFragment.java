package com.dtavana.foodswipe.fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.dtavana.foodswipe.R;
import com.dtavana.foodswipe.databinding.FragmentCycleBinding;
import com.dtavana.foodswipe.models.Restaurant;
import com.dtavana.foodswipe.models.RestaurantFilter;
import com.dtavana.foodswipe.network.FoodSwipeApplication;
import com.dtavana.foodswipe.network.FoodSwipeClient;
import com.dtavana.foodswipe.utils.OnSwipeTouchListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;

public class CycleFragment extends Fragment {

    public static final String TAG = "CycleFragment";

    FragmentCycleBinding binding;

    HashMap<FilterFragment.ALL_FILTERS, List<RestaurantFilter>> filterCache;
    Location location;

    List<Restaurant> restaurants;
    ArrayList<Restaurant> accepted;
    ArrayList<Restaurant> denied;
    int currentRestaurant = 0;
    
    FoodSwipeClient client;

    public CycleFragment() {
    }

    public static CycleFragment newInstance(HashMap<FilterFragment.ALL_FILTERS, List<RestaurantFilter>> filterCache, Location location) {
        Bundle args = new Bundle();
        args.putSerializable("filterCache", filterCache);
        args.putParcelable("location", location);

        CycleFragment fragment = new CycleFragment();
        fragment.setArguments(args);
        return fragment;
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
        Bundle args = getArguments();
        filterCache = (HashMap<FilterFragment.ALL_FILTERS, List<RestaurantFilter>>) args.getSerializable("filterCache");
        location = args.getParcelable("location");
        loadRestaurants();

        binding.getRoot().setOnTouchListener(new OnSwipeTouchListener(this, binding.rlRestaurant) {
            @Override
            public void onSwipeLeft(Animation a) {
                Log.d(TAG, "onSwipeLeft: Swiping left");
                denied.add(restaurants.get(currentRestaurant));
                currentRestaurant++;
                binding.rlRestaurant.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                binding.rlRestaurant.startAnimation(a);
            }

            @Override
            public void onSwipeRight(Animation a) {
                Log.d(TAG, "onSwipeRight: Swiping right");
                accepted.add(restaurants.get(currentRestaurant));
                currentRestaurant++;
                binding.rlRestaurant.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                binding.rlRestaurant.startAnimation(a);
            }
        });

        binding.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Click detected");
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(restaurants.get(currentRestaurant).getName())
                        .setMessage(restaurants.get(currentRestaurant).toString())
                        .show();
            }
        });
    }

    @SuppressLint("NewApi")
    private void loadRestaurants() {
        // TODO: Progress bar
        if(restaurants == null || restaurants.size() == 0) {
            Log.d(TAG, "loadRestaurants: Caching restaurants from API");
            final RequestParams params = new RequestParams();
            params.put("lat", (long) location.getLatitude());
            params.put("lon", (long) location.getLongitude());
            params.put("cuisines", restaurantFilterToString(FilterFragment.ALL_FILTERS.CUISINE));
            params.put("establishments", restaurantFilterToString(FilterFragment.ALL_FILTERS.ESTABLISHMENT));
            params.put("categories", restaurantFilterToString(FilterFragment.ALL_FILTERS.CATEGORY));

            client.getRestaurants(params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONObject data = json.jsonObject;
                    Log.d(TAG, "onSuccess: Successfully loaded restaurants with response: " + data.toString());
                    try {
                        int count = data.getInt("results_shown");
                        if (count > 0) {
                            JSONArray allRestaurants = data.getJSONArray("restaurants");
                            if(allRestaurants.length() < 1) {
                                ErrorFragment fragment = ErrorFragment.newInstance("No restaurants could be found with those filters", ErrorFragment.ALL_DESTINATIONS.FILTER, "Filter");
                                getParentFragmentManager().beginTransaction().add(fragment, ErrorFragment.TAG).commit();
                            }
                            for (int i = 0; i < allRestaurants.length(); i++) {
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
        else {
            Log.d(TAG, "loadRestaurants: Restaurants were loaded, skipping API Call");
            showCurrentRestaurant();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String restaurantFilterToString(FilterFragment.ALL_FILTERS filter) {
        List<RestaurantFilter> list = filterCache.get(filter);
        List<String> stringList = new ArrayList<>();
        assert list != null;
        for(RestaurantFilter rF : list) {
            stringList.add("" + rF.getId());
        }
        return String.join(",", stringList);

    }

    public void showCurrentRestaurant() {
        Log.d(TAG, "showCurrentRestaurant: Showing restaurant #" + currentRestaurant);
        // TODO: Limit number of restaurants that can be visited
        if(currentRestaurant >= restaurants.size()) {
            Log.d(TAG, "showCurrentRestaurant: Finished iterating through restaurants");
            binding.getRoot().setOnTouchListener(null);
            if(accepted.size() == 0) {
                Log.d(TAG, "showCurrentRestaurant: No accepted restaurants, showing error message");
                ErrorFragment fragment = ErrorFragment.newInstance("No restaurants could be found with those filters", ErrorFragment.ALL_DESTINATIONS.FILTER, "Filter");
                getParentFragmentManager().beginTransaction().replace(R.id.flContainer, fragment, ErrorFragment.TAG).commit();
            }
            else {
                ListFragment fragment = ListFragment.newInstance(accepted, denied);
                getParentFragmentManager().beginTransaction().replace(R.id.flContainer, fragment, ListFragment.TAG).addToBackStack(ListFragment.TAG).commit();
            }
            return;
        }
        Restaurant restaurant = restaurants.get(currentRestaurant);
        binding.tvName.setText(restaurant.getName());
        binding.tvCuisines.setText(restaurant.getCuisines());
        Number visitedCount = restaurant.getVisitedCount();
        String visitedCountString = String.format("Visited %d time(s)", visitedCount == null ? 0 : visitedCount);
        binding.tvVisitedCount.setText(visitedCountString);
//        Log.d(TAG, "showCurrentRestaurant: ImageURL: " + restaurant.getPhotosUrl());
//        // TODO: getImageURL is now a website rather than an image URL
//        GlideApp
//                .with(this)
//                .load(restaurant.getImageUrl())
//                .into(binding.ivPicture);
    }
}