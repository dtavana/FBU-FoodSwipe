package com.dtavana.foodswipe.utils;

import android.location.Location;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dtavana.foodswipe.R;
import com.dtavana.foodswipe.fragments.CycleFragment;
import com.dtavana.foodswipe.fragments.FilterFragment;
import com.dtavana.foodswipe.fragments.FinalFragment;
import com.dtavana.foodswipe.models.Restaurant;
import com.dtavana.foodswipe.models.RestaurantFilter;

import java.util.HashMap;
import java.util.List;

public class FilterFirst {

    public static final String LOG_TAG = "FilterFirst";
    public static void run(FragmentManager manager, HashMap<FilterFragment.ALL_FILTERS, List<RestaurantFilter>> filterCache, Location location, Restaurant restaurant) {
        Fragment fragment;
        String TAG;

        if(SetupNavigation.chosen) {
            Log.d(LOG_TAG, "run: Running FinalFragment");
            TAG = FinalFragment.TAG;
            fragment = manager.findFragmentByTag(TAG);
            if(fragment == null) {
                fragment = FinalFragment.newInstance(restaurant);
            }
        }
        else if(SetupNavigation.filtered) {
            Log.d(LOG_TAG, "run: Running CycleFragment");
            TAG = CycleFragment.TAG;
            fragment = manager.findFragmentByTag(TAG);
            if(fragment == null) {
                fragment = CycleFragment.newInstance(filterCache, location);
            }
        }
        else {
            Log.d(LOG_TAG, "run: Running FilterFragment");
            TAG = FilterFragment.TAG;
            fragment = manager.findFragmentByTag(TAG);
            if(fragment == null) {
                fragment = new FilterFragment();
            }
        }
        manager.beginTransaction().replace(R.id.flContainer, fragment, TAG).addToBackStack(TAG).commit();
    }

}
