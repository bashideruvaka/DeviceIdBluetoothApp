package com.database;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by eruvaka on 09-08-2016.
 */
public class RetroHelper {
    public static final String URL="http://52.77.24.190/eruvaka_live/".toString();
    //public static final String URL="http://eruvaka.com/".toString();
    private static final String LOG_TAG = RetroHelper.class.getSimpleName();
    Context mContext;

    public static RestAdapter getAdapter(Context context, String endpoint, String header) {
        String url = URL + endpoint;
        Log.e("LOGIN", "getAdapter url :: " + url);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(90, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(90, TimeUnit.SECONDS);    // socket timeout
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(url)
                //.setRequestInterceptor(getRequestInterceptor(header))
                .setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS).setLog(new RestAdapter.Log() { //setting to log level FULL causes Out of Memory Error!!
                    @Override
                    public void log(String msg) {
                        Log.e("Retro Helper", msg);
                    }
                })
                .setClient(new OkClient(client))
                .build();
        return restAdapter;
    }

}
