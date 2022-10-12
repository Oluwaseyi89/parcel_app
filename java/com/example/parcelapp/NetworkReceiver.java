package com.example.parcelapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {

    ConnectivityManager connectivityManager;

    public NetworkReceiver(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null) {

            boolean isWiFiAvailable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            boolean isGSMAvailable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();

            if(isWiFiAvailable) {
                Toast.makeText(context, "WiFi Reconnected", Toast.LENGTH_LONG).show();
            } else if(isGSMAvailable) {
                Toast.makeText(context, "GSM Data Available", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Network Not Available", Toast.LENGTH_LONG).show();
            }
        }

    }
}
