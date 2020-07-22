package com.dtavana.foodswipe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.dtavana.foodswipe.databinding.FragmentDetailBinding;
import com.dtavana.foodswipe.models.Restaurant;

public class DetailFragment extends DialogFragment {

    public static final String TAG = "DetailFragment";

    FragmentDetailBinding binding;

    public DetailFragment() {}

    public static DetailFragment newInstance(Restaurant restaurant) {
        Bundle args = new Bundle();
        args.putParcelable(Restaurant.class.getSimpleName(), restaurant);
        
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        binding = FragmentDetailBinding.inflate(getActivity().getLayoutInflater(), parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;
        Restaurant restaurant = args.getParcelable(Restaurant.class.getSimpleName());
        assert restaurant != null;
        binding.tvName.setText(restaurant.getName());
        binding.tvCuisines.setText(restaurant.getCuisines());
        Number visitedCount = restaurant.getVisitedCount();
        String visitedCountString = String.format("Visited %d time(s)", visitedCount == null ? 0 : visitedCount);
        binding.tvVisitedCount.setText(visitedCountString);
        String address = String.format("%s, %s", restaurant.getAddress(), restaurant.getCity());
        binding.tvAddress.setText(address);
        binding.tvTimings.setText(restaurant.getTimings());
        Restaurant.Rating rating = restaurant.getRating();
        binding.tvVotes.setText(rating.getVotesString());
        binding.tvRating.setText(rating.getRatingString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}