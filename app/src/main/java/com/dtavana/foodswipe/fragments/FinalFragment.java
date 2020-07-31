package com.dtavana.foodswipe.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dtavana.foodswipe.databinding.FragmentFinalBinding;
import com.dtavana.foodswipe.models.Restaurant;

public class FinalFragment extends Fragment {

    public static final String TAG = "FinalFragment";

    FragmentFinalBinding binding;

    Restaurant restaurant;

    public FinalFragment() {}

    public static FinalFragment newInstance(Restaurant restaurant) {
        Bundle args = new Bundle();
        args.putParcelable("restaurant", restaurant);

        FinalFragment fragment = new FinalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        binding = FragmentFinalBinding.inflate(getLayoutInflater(), parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        restaurant = args.getParcelable("restaurant");

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
        binding.btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Navigate to address
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + restaurant.getAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }
}
