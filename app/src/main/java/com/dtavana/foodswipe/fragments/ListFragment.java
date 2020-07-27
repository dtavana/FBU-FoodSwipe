package com.dtavana.foodswipe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dtavana.foodswipe.adapters.RestaurantsAdapter;
import com.dtavana.foodswipe.databinding.FragmentListBinding;
import com.dtavana.foodswipe.models.Restaurant;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    public static final String TAG = "ListFragment";

    FragmentListBinding binding;

    RestaurantsAdapter adapter;

    ArrayList<Restaurant> accepted;
    ArrayList<Restaurant> denied;

    public ListFragment() {}

    public static ListFragment newInstance(ArrayList<Restaurant> accepted, ArrayList<Restaurant> denied) {
        Bundle args = new Bundle();
        args.putSerializable("accepted", accepted);
        args.putSerializable("denied", denied);

        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        binding = FragmentListBinding.inflate(getLayoutInflater(), parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Bundle args = getArguments();

        accepted = (ArrayList<Restaurant>) args.getSerializable("accepted");
        denied = (ArrayList<Restaurant>) args.getSerializable("denied");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rvRestaurants.setLayoutManager(layoutManager);
        adapter = new RestaurantsAdapter(getContext(), accepted, denied);
        binding.rvRestaurants.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        binding.rvRestaurants.addItemDecoration(dividerItemDecoration);

        showAccepted();
    }

    private void showAccepted() {
        //TODO: If only one restaurant exists, go straight to final restaurant fragment
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}