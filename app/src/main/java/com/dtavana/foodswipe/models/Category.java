package com.dtavana.foodswipe.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Category extends RestaurantFilter {

    public static Category fromJson(JSONObject obj) throws JSONException {
        Category category = new Category();
        category.setId(obj.getInt("id"));
        category.setName(obj.getString("name"));
        return category;
    }

}
