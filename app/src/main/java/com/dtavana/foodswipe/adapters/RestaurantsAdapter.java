package com.dtavana.foodswipe.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dtavana.foodswipe.R;
import com.dtavana.foodswipe.databinding.ItemRestaurantBinding;
import com.dtavana.foodswipe.fragments.DetailFragment;
import com.dtavana.foodswipe.fragments.FinalFragment;
import com.dtavana.foodswipe.models.Restaurant;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Objects;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {
    public static final String TAG = "RestaurantsAdapter";

    Context ctx;
    List<Restaurant> restaurants;
    List<Restaurant> denied;

    public RestaurantsAdapter(Context context, List<Restaurant> restaurants, List<Restaurant> denied) {
        this.ctx = context;
        this.restaurants = restaurants;
        this.denied = denied;
    }

    public void clear() {
        restaurants.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRestaurantBinding binding = ItemRestaurantBinding.inflate(LayoutInflater.from(ctx), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemRestaurantBinding binding;

        Restaurant restaurant;

        public ViewHolder(ItemRestaurantBinding binding) {
            super(binding.getRoot());
            binding.getRoot().setOnClickListener(this);
            this.binding = binding;
        }

        public void bind(final Restaurant restaurant) {
            this.restaurant = restaurant;
            binding.tvName.setText(restaurant.getName());
            binding.tvCuisine.setText(restaurant.getCuisines());
            binding.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cacheRestaurant();
                    moveToFinalFragment();
                }
            });
            binding.btnDeny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = restaurants.indexOf(restaurant);
                    restaurants.remove(restaurant);
                    denied.add(restaurant);
                    notifyItemRemoved(index);
                }
            });
        }

        private void moveToFinalFragment() {
            FragmentManager manager = ((AppCompatActivity) ctx).getSupportFragmentManager();
            Fragment fragment = FinalFragment.newInstance(restaurant);
            String TAG = FinalFragment.TAG;
            manager.beginTransaction().replace(R.id.flContainer, fragment, TAG).commit();
        }

        private void cacheRestaurant() {
            ParseQuery<Restaurant> query = ParseQuery.getQuery(Restaurant.class);
            query.whereEqualTo(Restaurant.KEY_RESTAURANTID, restaurant.getRestaurantId());
            query.findInBackground(new FindCallback<Restaurant>() {
                @Override
                public void done(List<Restaurant> objects, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "done: Error checking if restaurant already exists", e);
                        return;
                    }
                    if (objects == null || objects.size() < 1) {
                        Log.d(TAG, "done: Could not find existing restaurant, saving new restaurant");
                        restaurant.setVisitedCount(restaurant.getVisitedCount().intValue() + 1);
                        restaurant.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "done: Error occurred while caching chosen restaurant", e);
                                    return;
                                }
                                Log.d(TAG, "done: Cached new restaurant to attend");
                            }
                        });
                    } else {
                        Log.d(TAG, "done: Found existing restaurant to attend, updating visitedCount");
                        Restaurant cachedRestaurant = objects.get(0);
                        Log.d(TAG, "done: Old visitedCount: " + cachedRestaurant.getNumber(Restaurant.KEY_VISITEDCOUNT));
                        Number newVisitedCount = Objects.requireNonNull(cachedRestaurant.getNumber(Restaurant.KEY_VISITEDCOUNT)).intValue() + 1;
                        Log.d(TAG, "done: New visitedCount: " + newVisitedCount);
                        cachedRestaurant.setVisitedCount(newVisitedCount);
                        // For caching reasons
                        restaurant.setVisitedCount(newVisitedCount);
                        cachedRestaurant.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "done: Error occurred while incrementing visitedCount chosen restaurant", e);
                                    return;
                                }
                                Log.d(TAG, "done: Incremented an existing restaurant visitedCount");
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: Click detected");
            FragmentManager manager = ((AppCompatActivity) ctx).getSupportFragmentManager();
            Fragment fragment = DetailFragment.newInstance(restaurant);
            String TAG = DetailFragment.TAG;
            manager.beginTransaction().add(fragment, TAG).commit();
        }
    }
}
