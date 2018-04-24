package com.aa.calldefender;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import java.lang.reflect.Method;

//https://stackoverflow.com/questions/15945952/no-such-method-getitelephony-to-disconnect-call

//Class for the handling of phone calls and blocking
public class CallInterceptor extends BroadcastReceiver {

    //Declare variables
    boolean num_exist;
    SmsManager smsManager = SmsManager.getDefault();
    String incomingNumber;
    String state;
    DHelper database;
    SharedPreferences sharedPref;
    boolean sms;
    boolean call_deletion;
    boolean block_switch;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) { //When a phone call is received (when broadcast receiver is on)

        this.context = context; //Save context
        //Access and save shared preferences to variables
        sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        sms = sharedPref.getBoolean("sms", false);
        call_deletion = sharedPref.getBoolean("call_del", false);
        block_switch = sharedPref.getBoolean("block_all", false);

        incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); //Save incoming number to a variables
        state = intent.getStringExtra(TelephonyManager.EXTRA_STATE); //Save the call state to a variable



        //Run async DB query that determine if the incoming number should be blocked
        new BackgroundNumberExist(context, this).execute(incomingNumber);

    }

    public void block_call(Context context) { //Function that will block the incoming call
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

    //Function used by the "post execute()" of 'BackgroundNumberExist'
    public void exist(boolean number){

            num_exist = number; //Save query result to a variable

        //If the phone is ringing and only numbers/area codes in DB should be blocked
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) && !block_switch) {

            if (num_exist) { //If the incoming number is in the DB or contains an area code in the DB
                block_call(context); //Call function to block the call

                if (sms) { //If SMS sending has been enabled send the incoming caller a warning text
                    smsManager.sendTextMessage(incomingNumber, null, "The person you are trying to call would not like to be contacted at this time!", null, null);
                }

                //Thread sleep required for call log deletion to properly work
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (call_deletion) { //If call log deletion has been enabled

                    //Request permissions if required
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    //Delete call log for incoming number
                    context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, "NUMBER=" + "'" + incomingNumber + "'", null);
                }
            }
        }

        else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) { //else if phone is ringing

            block_call(context); //call function to block the call

            if (sms) { //If SMS sending has been enabled send the incoming caller a warning text
                smsManager.sendTextMessage(incomingNumber, null, "test", null, null);
            }

            //Thread sleep required for call log deletion to properly work
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (call_deletion){ //If call log deletion has been enabled delete the call log for the incoming number
                context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, "NUMBER=" + "'"+ incomingNumber +"'", null);
            }
        }
    }

}
