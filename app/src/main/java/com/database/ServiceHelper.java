package com.database;

import com.google.gson.JsonObject;

/**
 * Created by eruvaka on 09-08-2016.
 */
public interface ServiceHelper {

    @retrofit.http.Headers("Accept:application/json")
    @retrofit.http.POST("/user/login")
    public void loginUser(@retrofit.http.Body JsonObject obj, retrofit.Callback<JsonObject> callback);

    @retrofit.http.Headers("Accept:application/json")
    @retrofit.http.POST("/user/getfeeders")
    public void getfeeders(@retrofit.http.Body JsonObject obj, retrofit.Callback<JsonObject> callback);

    @retrofit.http.Headers("Accept:application/json")
    @retrofit.http.POST("/user/getsinglefeederfeeders")
    public void getsinglefeeder(@retrofit.http.Body JsonObject obj, retrofit.Callback<JsonObject> callback);

    @retrofit.http.Headers("Accept:application/json")
    @retrofit.http.POST("/user/feeders")
    public void updateschedules(@retrofit.http.Body JsonObject obj, retrofit.Callback<JsonObject> callback);

    @retrofit.http.Headers("Accept:application/json")
    @retrofit.http.POST("/user/getfeederlogs")
    public void getfeederlogs(@retrofit.http.Body JsonObject obj, retrofit.Callback<JsonObject> callback);

    @retrofit.http.Headers("Accept:application/json")
    @retrofit.http.POST("/user/updatesettings")
    public void updatesettings(@retrofit.http.Body JsonObject obj, retrofit.Callback<JsonObject> callback);


}
