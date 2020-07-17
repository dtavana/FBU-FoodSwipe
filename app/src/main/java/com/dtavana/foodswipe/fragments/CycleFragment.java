package com.dtavana.foodswipe.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dtavana.foodswipe.databinding.FragmentCycleBinding;
import com.dtavana.foodswipe.utils.OnSwipeTouchListener;

public class CycleFragment extends Fragment {

    public static final String TAG = "CycleFragment";

    FragmentCycleBinding binding;

    public CycleFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        binding = FragmentCycleBinding.inflate(getLayoutInflater(), parent, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.getRoot().setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                Log.d(TAG, "onSwipeLeft: Swiping left");
            }
            @Override
            public void onSwipeRight() {
                Log.d(TAG, "onSwipeRight: Swiping right");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}