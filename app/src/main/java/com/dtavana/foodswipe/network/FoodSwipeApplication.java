package com.dtavana.foodswipe.network;

import android.app.Application;
import android.content.Context;

import com.dtavana.foodswipe.R;
import com.dtavana.foodswipe.models.Restaurant;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class FoodSwipeApplication extends Application {

    static FoodSwipeClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        setupParse();
        setupRestClient();
    }

    private void setupParse() {
        ParseObject.registerSubclass(Restaurant.class);

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.PARSE_APPID)) // should correspond to APP_ID env variable
                .clientKey(getString(R.string.PARSE_CLIENTKEY))  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server(getString(R.string.PARSE_SERVER)).build());
    }

    private void setupRestClient() {
        client = new FoodSwipeClient(this);
    }

    public static FoodSwipeClient getClient() {
        return client;
    }
}