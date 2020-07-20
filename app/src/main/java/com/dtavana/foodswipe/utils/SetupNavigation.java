package com.dtavana.foodswipe.utils;

import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dtavana.foodswipe.R;
import com.dtavana.foodswipe.databinding.ActivityMainBinding;
import com.dtavana.foodswipe.fragments.CycleFragment;
import com.dtavana.foodswipe.fragments.ListFragment;
import com.dtavana.foodswipe.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SetupNavigation {
    public static void run(final FragmentManager manager, ActivityMainBinding binding) {
        binding.navigation.getRoot().setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                String TAG;
                switch (item.getItemId()) {
                    case R.id.miList:
                        TAG = ListFragment.TAG;
                        fragment = manager.findFragmentByTag(TAG);
                        if(fragment == null) {
                            fragment = new ListFragment();
                        }
                        break;
                    case R.id.miCycle:
                    default:
                        TAG = CycleFragment.TAG;
                        fragment = manager.findFragmentByTag(TAG);
                        if(fragment == null) {
                            fragment = new CycleFragment();
                        }
                        break;
                    case R.id.miProfile:
                        TAG = ProfileFragment.TAG;
                        fragment = manager.findFragmentByTag(TAG);
                        if(fragment == null) {
                            fragment = new ProfileFragment();
                        }
                        break;
                }
                manager.beginTransaction().replace(R.id.flContainer, fragment, TAG).addToBackStack(TAG).commit();
                return true;
            }
        });
        binding.navigation.getRoot().setSelectedItemId(R.id.miCycle);
    }
}
