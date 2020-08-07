package com.dtavana.foodswipe.utils;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dtavana.foodswipe.R;
import com.dtavana.foodswipe.databinding.ActivityMainBinding;
import com.dtavana.foodswipe.fragments.ListFragment;
import com.dtavana.foodswipe.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SetupNavigation {

    public static boolean filtered = false;
    public static boolean chosen = false;

    public static void run(final FragmentManager manager, ActivityMainBinding binding) {
        binding.navigation.getRoot().setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                String TAG = null;
                boolean skip = false;
                switch (item.getItemId()) {
                    case R.id.miList:
                        if(chosen && filtered) {
                            TAG = ListFragment.TAG;
                            fragment = manager.findFragmentByTag(TAG);
                            if(fragment == null) {
                                fragment = new ListFragment();
                            }
                        } else {
                            skip = true;
                        }
                        break;
                    case R.id.miCycle:
                    default:
                        FilterFirst.run(manager, null, null, null);
                        return true;
                    case R.id.miProfile:
                        TAG = ProfileFragment.TAG;
                        fragment = manager.findFragmentByTag(TAG);
                        if(fragment == null) {
                            fragment = new ProfileFragment();
                        }
                        break;
                }
                if(!skip) {
                    manager.beginTransaction().replace(R.id.flContainer, fragment, TAG).addToBackStack(TAG).commit();
                }
                return true;
            }
        });
        binding.navigation.getRoot().setSelectedItemId(R.id.miCycle);
    }
}
