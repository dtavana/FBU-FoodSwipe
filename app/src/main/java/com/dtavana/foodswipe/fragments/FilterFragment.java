package com.dtavana.foodswipe.fragments;

import android.Manifest;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.dtavana.foodswipe.adapters.FiltersAdapter;
import com.dtavana.foodswipe.databinding.FragmentFilterBinding;
import com.dtavana.foodswipe.models.Category;
import com.dtavana.foodswipe.models.Cuisine;
import com.dtavana.foodswipe.models.Establishment;
import com.dtavana.foodswipe.models.RestaurantFilter;
import com.dtavana.foodswipe.network.FoodSwipeApplication;
import com.dtavana.foodswipe.network.FoodSwipeClient;
import com.dtavana.foodswipe.utils.FilterFirst;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;

public class FilterFragment extends Fragment {

    public static final String TAG = "FilterFragment";

    public static final int PERMISSION_REQUEST_CODE = 999;

    List<RestaurantFilter> filters;
    FiltersAdapter adapter;
    Location location;
    public enum ALL_FILTERS {
        CUISINE,
        ESTABLISHMENT,
        CATEGORY
    }
    ALL_FILTERS currentFilter = ALL_FILTERS.CATEGORY;
    HashMap<ALL_FILTERS, List<RestaurantFilter>> filterCache;

    FoodSwipeClient client;
    RequestParams params;

    FragmentFilterBinding binding;

    public FilterFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = FoodSwipeApplication.getClient();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        binding = FragmentFilterBinding.inflate(getLayoutInflater(), parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        filters = new ArrayList<>();
        filterCache = new HashMap<>();
        binding.rvFilters.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FiltersAdapter(getContext(), filters);
        binding.rvFilters.setAdapter(adapter);
        loadLocation();
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
            loadLocation();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupSearchButton() {
        binding.btnSearch.setText("Search");
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Fix because we need to cache the last set of checkboxes
                cacheCheckboxes();

                FilterFirst.filtered = true;
                FilterFirst.run(getParentFragmentManager(), filterCache, location);
            }
        });
    }

    private void setupNextButton() {
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cacheCheckboxes();
                adapter.clear();
                if(currentFilter == ALL_FILTERS.CATEGORY) {
                    currentFilter = ALL_FILTERS.ESTABLISHMENT;
                    loadEstablishments();
                }
                else if(currentFilter == ALL_FILTERS.ESTABLISHMENT) {
                    currentFilter = ALL_FILTERS.CUISINE;
                    setupSearchButton();
                    loadCuisines();
                }
            }
        });
    }

    private void cacheCheckboxes() {
        ArrayList<RestaurantFilter> checkedFilters = new ArrayList<>();
        for(RestaurantFilter filter : filters) {
            if(filter.isChecked()) {
                Log.d(TAG, "cacheCheckboxes: " + filter.getName() + " is checked, caching");
                checkedFilters.add(filter);
            }
        }
        filterCache.put(currentFilter, checkedFilters);
    }


    private void loadLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "loadRestaurants: Requesting permissions for location");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location loc) {
                if (loc == null) {
                    Log.e(TAG, "onSuccess: Error loading location");
                    return;
                }
                location = loc;
                params = new RequestParams();
                params.put("lat", (long) location.getLatitude());
                params.put("lon", (long) location.getLongitude());
                setupNextButton();
                loadCategories();
            }
        });
    }

    private void loadEstablishments() {
        Log.d(TAG, "loadEstablishments: Loading establishments");
        client.getEstablishments(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Log.d(TAG, "onSuccess: Successfully received establishments. Now parsing.");
                    JSONArray arr = json.jsonObject.getJSONArray("establishments");
                    Log.d(TAG, "onSuccess: JSON: " + json.toString());
                    for(int i = 0; i < arr.length(); i++) {
                        Establishment establishment = Establishment.fromJson(arr.getJSONObject(i).getJSONObject("establishment"));
                        filters.add(establishment);
                    }
                    Log.d(TAG, "onSuccess: Finished loading establishments");
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: Error loading establishments", throwable);
            }
        });
    }

    private void loadCuisines() {
        Log.d(TAG, "loadCuisines: Loading cuisines");
        client.getCuisines(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Log.d(TAG, "onSuccess: Successfully received cuisines. Now parsing.");
                    JSONArray arr = json.jsonObject.getJSONArray("cuisines");
                    Log.d(TAG, "onSuccess: JSON: " + json.toString());
                    for(int i = 0; i < arr.length(); i++) {
                        Cuisine cuisine = Cuisine.fromJson(arr.getJSONObject(i).getJSONObject("cuisine"));
                        filters.add(cuisine);
                    }
                    Log.d(TAG, "onSuccess: Finished loading cuisines");
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: Error loading cuisines", throwable);
            }
        });
    }

    private void loadCategories() {
        Log.d(TAG, "loadCategories: Loading categories");
        client.getCategories(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Log.d(TAG, "onSuccess: Successfully received categories. Now parsing.");
                    JSONArray arr = json.jsonObject.getJSONArray("categories");
                    Log.d(TAG, "onSuccess: JSON: " + json.toString());
                    for(int i = 0; i < arr.length(); i++) {
                        Category category = Category.fromJson(arr.getJSONObject(i).getJSONObject("categories"));
                        filters.add(category);
                    }
                    Log.d(TAG, "onSuccess: Finished loading categories");
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure: Error loading categories", throwable);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}