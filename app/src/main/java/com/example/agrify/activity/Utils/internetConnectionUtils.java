package com.example.agrify.activity.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import es.dmoral.toasty.Toasty;

public class internetConnectionUtils {
  public   static  boolean isInternetConnected(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            Toasty.error(context,"check your internet connection",Toasty.LENGTH_SHORT).show();
        }
        return  isConnected;
    }
}
