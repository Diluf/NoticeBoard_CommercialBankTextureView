package com.c3labs.dss_CommercialBank.Controls;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.c3labs.dss_CommercialBank.R;
import com.c3labs.dss_CommercialBank.Recievers.MyBroadCastReciever;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by c3 on 2/6/2018.
 */

public class Methods {
    public static SharedPreferences getSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getNodeId(Context context) {
        return getSharedPref(context).getString("nodeId", "");
    }

    public static String getBranchName(Context context) {
        return getSharedPref(context).getString("branchName", "");
    }

    public void setDateToSystem(String dateString) {
//        July 11, 2016 2:15 that would be date 0711141516
        try {
            String month = dateString.substring(4, 6);
            String date = dateString.substring(6, 8);
            String hour = dateString.substring(9, 11);
            String min = dateString.substring(11, 13);
            String year = dateString.substring(0, 4);

            Log.d("?????????????????????", "setDateToSystem: " + month + date + hour + min + year);
            saveToTextFile((year + "-" + month + "-" + date + "  " + hour + "-" + min) + "", "/login_reports.txt");

            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("date " + month + date + hour + min + year + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File createOrGetDirectory() {
        File myDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/C3DSS");
        if (!myDirectory.exists()) {
            myDirectory.mkdirs();
        }
        return myDirectory;
    }

    public int getNetworkStatusIcon(Context context) {
        if (MyBroadCastReciever.isNetworkConnected(context)) {
            return R.drawable.online;
        }
        return R.drawable.offline;
    }

    public void restartDevice() {
        try {
            Runtime.getRuntime().exec("reboot");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToTextFile(String value, String fileName) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(Methods.createOrGetDirectory().toString() + fileName, true)));
            if (fileName.equalsIgnoreCase("/errors.txt")) {
                out.println(new Date());
            }
            out.println(value);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
