package com.c3labs.dss_CommercialBank.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.c3labs.dss_CommercialBank.Splash;

/**
 * Created by c3 on 2/6/2018.
 */

public class MyBroadCastReciever extends BroadcastReceiver {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, intent + "Myyyyy----------", Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, (context instanceof Splash) + "Myyyyy-------Splash", Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, (context instanceof FullscreenActivity) + "Myyyyy-------Full", Toast.LENGTH_SHORT).show();
//        Log.d("-------=====", "onReceive: " + (context instanceof Splash));
//        Log.d("-------=====", "onReceive: " + (context instanceof FullscreenActivity));

//        if (isNetworkConnected(context)) {
        ((Splash) context).showLoginDialogOrContinue();
//        } else {
//            ((Splash) context).showLoginDialogOrContinue();
//        }

    }
}
