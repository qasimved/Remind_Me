package com.example.qasim1793.remindme.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetUtils {


    public static boolean isNetConnected(Context context) {
        try {
            ConnectivityManager connManger = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connManger != null) {
                NetworkInfo netInfo = connManger.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    if (netInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            Toast.makeText(context, "Connectivity Manager is not detected", Toast.LENGTH_LONG).show();

        }
        return false;


    }

}