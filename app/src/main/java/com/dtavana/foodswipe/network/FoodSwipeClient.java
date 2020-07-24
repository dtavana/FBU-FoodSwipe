package com.dtavana.foodswipe.network;

import android.content.Context;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.dtavana.foodswipe.R;

public class FoodSwipeClient extends AsyncHttpClient {

    public static final String TAG = "FoodSwipeClient";
    public static final String BASE_URL = "https://developers.zomato.com/api/v2.1/";

    RequestHeaders baseHeaders;

    public FoodSwipeClient(Context ctx) {
        baseHeaders = new RequestHeaders();
        baseHeaders.put("user_key", ctx.getString(R.string.ZOMATO_API_KEY));
    }

    public void getRestaurants(RequestParams params, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search");
        Log.d(TAG, "getRestaurants: Resolved apiUrl: " + apiUrl);
        get(apiUrl, baseHeaders, params, handler);
    }

    public void getRestaurant(String restaurantId, JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("res_id", restaurantId);
        String apiUrl = getApiUrl("restaurants" + restaurantId);
        Log.d(TAG, "getRestaurant: Resolved apiUrl: " + apiUrl);
        get(apiUrl, baseHeaders, params, handler);
    }

    public void getCategories(RequestParams params, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("categories");
        Log.d(TAG, "getCategories: Resolved apiIrl: " + apiUrl);
        get(apiUrl, baseHeaders, params, handler);
    }

    public void getCuisines(RequestParams params, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("cuisines");
        Log.d(TAG, "getCuisines: Resolved apiIrl: " + apiUrl);
        get(apiUrl, baseHeaders, params, handler);
    }

    public void getEstablishments(RequestParams params, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("establishments");
        Log.d(TAG, "getCuisines: Resolved apiIrl: " + apiUrl);
        get(apiUrl, baseHeaders, params, handler);
    }

    private String getApiUrl(String endpoint) {
        return String.format("%s%s", BASE_URL, endpoint);
    }
}
