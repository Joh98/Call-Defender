package com.aa.calldefender;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import java.lang.reflect.Method;

//Class for the handling of phone calls and blocking
public class CallInterceptor extends BroadcastReceiver {

    //Declare variables
    boolean num_exist, sms, call_deletion, block_all;
    SmsManager smsManager = SmsManager.getDefault();
    String incoming_number, state;
    SharedPreferences sharedPref;
    Context context;
    int blocked_calls_tally;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) { //When a phone call is received (when broadcast receiver is on)
        this.context = context; //Save context

        //Access and save shared preferences to variables
        sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        sms = sharedPref.getBoolean("sms", false); //Boolean value that determines if an sms should be sent to blocked number
        call_deletion = sharedPref.getBoolean("call_del", false); //Boolean value that determines if the call log entry of the blocked call should be deleted
        block_all = sharedPref.getBoolean("block_all", false); //Boolean value that determines if all numbers should be blocked or just those in the DB

        incoming_number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); //Save incoming number to a variable
        state = intent.getStringExtra(TelephonyManager.EXTRA_STATE); //Save the call state to a variable

        //Run async DB task that determines if the incoming number should be blocked
        new BackgroundNumberExist(context, this).execute(incoming_number);
    }

    public void blockCall(Context context) { //Function that will block the incoming call
        try {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); //Access Telephony
            Class classTelephony = Class.forName(telephonyManager.getClass().getName()); //Get an instance of the Telephony Class

            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony"); //Get access to the 'getITelephony' function
            methodGetITelephony.setAccessible(true);
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager); //Invoke 'getITelephony' method onto an object

            Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName()); //Get an instance of the ITelephony Class
            Method silence_ringer = telephonyInterfaceClass.getDeclaredMethod("silenceRinger"); //Get access to  the 'silenceRinger' function
            Method end_call = telephonyInterfaceClass.getDeclaredMethod("endCall"); //Get access to the 'endCall' function

           silence_ringer.invoke(telephonyInterface); //Invoke 'silenceRinger' so the phone ringer is silenced
            end_call.invoke(telephonyInterface); //Invoke 'endCall' so the phone call is automatically hung up
            setResultData(null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Function used by the 'onPostExecute' of 'BackgroundNumberExist'
    public void exist(boolean number){

            num_exist = number; //Save query result to a variable

        //If the phone is ringing and only numbers/area codes in DB should be blocked
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING) && !block_all) {

            if (num_exist) { //If the incoming number is in the DB or contains an area code in the DB
                blockCall(context); //Call function to block the call

                if (sms) { //If SMS sending has been enabled send the incoming caller a warning text
                    smsManager.sendTextMessage(incoming_number, null, "The person you are trying to call would not like to be contacted at this time!", null, null);
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
                    context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, "NUMBER=" + "'" + incoming_number + "'", null);
                }

                //Add 1 to the 'tally' shared preference and send broadcast back to HomeFragment
                blocked_calls_tally = sharedPref.getInt("tally", 0);
                blocked_calls_tally +=1;
                editor = sharedPref.edit();
                editor.putInt("tally", blocked_calls_tally);
                editor.apply();
                context.sendBroadcast(new Intent("com.aa.calldefender.UPDATE_COUNT"));
            }
        }

        else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) { //Else if phone is ringing

            blockCall(context); //Call function to block the call

            if (sms) { //If SMS sending has been enabled send the incoming caller a warning text
                smsManager.sendTextMessage(incoming_number, null, "The person you are trying to call would not like to be contacted at this time!", null, null);
            }

            //Thread sleep required for call log deletion to properly work
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (call_deletion){ //If call log deletion has been enabled delete the call log for the incoming number
                context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, "NUMBER=" + "'"+ incoming_number +"'", null);
            }

            //Add 1 to the 'tally' shared preference and send broadcast back to HomeFragment
            blocked_calls_tally = sharedPref.getInt("tally", 0);
            blocked_calls_tally +=1;
            editor = sharedPref.edit();
            editor.putInt("tally", blocked_calls_tally);
            editor.apply();

            //Send broadcast to HomeFragment so that the tally of blocked calls is updated
            context.sendBroadcast(new Intent("com.aa.calldefender.UPDATE_COUNT"));
        }
    }
}
