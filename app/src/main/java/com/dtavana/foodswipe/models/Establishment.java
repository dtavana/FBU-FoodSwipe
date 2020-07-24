package com.dtavana.foodswipe.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Establishment extends RestaurantFilter {

    public static Establishment fromJson(JSONObject obj) throws JSONException {
        Establishment establishment = new Establishment();
        establishment.setId(obj.getInt("id"));
        establishment.setName(obj.getString("name"));
        return establishment;
    }

}
