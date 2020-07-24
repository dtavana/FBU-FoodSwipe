package com.dtavana.foodswipe.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Cuisine extends RestaurantFilter {

    public static Cuisine fromJson(JSONObject obj) throws JSONException {
        Cuisine cuisine = new Cuisine();
        cuisine.setId(obj.getInt("cuisine_id"));
        cuisine.setName(obj.getString("cuisine_name"));
        return cuisine;
    }

}
