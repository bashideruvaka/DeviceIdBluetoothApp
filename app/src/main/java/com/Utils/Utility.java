package com.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.security.Identity;

/**
 * Created by eruvaka on 01-07-2017.
 */

public class Utility {
    public static ProgressDialog dialog;
    public static String timezone;
    public static UserPreferenceData userPreferenceData;
    public static String last_update_time_str;
    public static String disp_feed;
    String last_update_date;
    Context context;
    ProgressDialog myDialog;
       public static String mSelect_device="NOT";
    //check network info
    public static boolean isNetworkConnected(Context c){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetworkInfo != null)&&(activeNetworkInfo.isConnected())){
            return true;
        }else{
            return false;
        }
    }
    ///added progress dialog
    public static ProgressDialog showProgressDialog(Context context, String text){
        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading Data Please Wait ...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }
    //hide progress dialog
    public static void hideLoader() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
    public static void showAlertDialog(final Context context , String title , String message){

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // setting Dialog title
        alertDialog.setTitle(title);

        // setting Dialog message
        alertDialog.setMessage(message);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

            }
        });


       /* alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });*/
        alertDialog.show();

    }

    public static void addChecked(String pg) {
        mSelect_device=pg;
    }

    public static String getSelect_Device() {
        return mSelect_device;
    }
}
