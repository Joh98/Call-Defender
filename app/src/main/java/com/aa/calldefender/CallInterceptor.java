package com.aa.calldefender;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;


//https://stackoverflow.com/questions/15945952/no-such-method-getitelephony-to-disconnect-call

/**
 * Created by admin on 09/02/2018.
 */

public class CallInterceptor extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SmsManager smsManager = SmsManager.getDefault();

        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.d("Database Helper", "Phone No = " + incomingNumber);
        DHelper database = new DHelper(context);



        SharedPreferences sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean sms = sharedPref.getBoolean("sms", false);
        boolean call_deletion = sharedPref.getBoolean("call_del", false);
        boolean block_switch = sharedPref.getBoolean("block_all", false);


        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) && !block_switch) {

            if (database.area_code_exist(incomingNumber, 1) || database.num_exist(incomingNumber)) {
                block_call(context);

                if (sms) {
                    smsManager.sendTextMessage(incomingNumber, null, "test", null, null);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (call_deletion){
                    context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, "NUMBER=" + "'"+ incomingNumber +"'", null);
                }
            }
        }

        else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

            block_call(context);

            if (sms) {
                smsManager.sendTextMessage(incomingNumber, null, "test", null, null);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (call_deletion){
                context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, "NUMBER=" + "'"+ incomingNumber +"'", null);
            }
        }
    }

public void block_call(Context context) {
    try {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // Get the getITelephony() method
        Class classTelephony = Class.forName(telephonyManager.getClass().getName());
        Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
        // Ignore that the method is supposed to be private
        methodGetITelephony.setAccessible(true);
        // Invoke getITelephony() to get the ITelephony interface
        Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
        // Get the endCall method from ITelephony
        Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
        //telephonyService.silenceRinger();
        Method methodSilence = telephonyInterfaceClass.getDeclaredMethod("silenceRinger");
        Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
        // Invoke endCall()

        methodSilence.invoke(telephonyInterface);
        methodEndCall.invoke(telephonyInterface);
        setResultData(null);

    }
    catch (Exception e) {
        e.printStackTrace();
    }
}

}




