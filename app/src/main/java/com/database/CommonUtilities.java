package com.database;

import android.content.Context;

/**
 * Created by eruvaka on 16-09-2016.
 */
public class CommonUtilities {

    public static ServiceHelper getBaseClassService(Context context, String url, String header) {
        return new RetroHelper().getAdapter(context, url, header).create(ServiceHelper.class);
    }
}
