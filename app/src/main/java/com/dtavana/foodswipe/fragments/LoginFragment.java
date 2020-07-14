package com.dtavana.foodswipe.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dtavana.foodswipe.databinding.FragmentLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";

    FragmentLoginBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        binding = FragmentLoginBinding.inflate(getLayoutInflater(), parent, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                loginOrSignup(username, password);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loginUser(final String username, final String password) {
        Log.i(TAG, "loginUser: Attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null) {
                    //TODO: Better error handling
                    Log.d(TAG, "done: Issue with login, trying signup");
                    signupUser(username, password);
                    return;
                }
                Log.i(TAG, "done: Finished logging in: " + username);
//                goMainActivity();
            }
        });
    }

    private void signupUser(final String username, final String password) {
        Log.i(TAG, "signupUser: Attempting to signup user " + username);
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    // TODO: Better error handling
                    Log.e(TAG, "done: Error signing up", e);
                    return;
                }
                Log.i(TAG, "done: Finished signing-up user: " + username);
                loginUser(username, password);
            }
        });
    }

    private void loginOrSignup(final String username, final String password) {
        // Try login first, if that doesn't work, try signup
        loginUser(username, password);
    }

//    private void goMainActivity() {
//        Intent i = new Intent(getActivity(), MainActivity.class);
//        startActivity(i);
//        Objects.requireNonNull(getActivity()).finish();
//    }

}