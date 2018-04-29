package com.aa.calldefender;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

//Class for the service that detects when the app has been removed via 'All Apps' view
public class RemovedFromAllApps extends Service {

    //Declare variable
    SharedPreferences.Editor editor = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) { //Function that gets called when the app has been removed via 'All Apps' view
        super.onTaskRemoved(rootIntent);
        dismissSettingsDialog(); //Call function to dismiss any dialogs open on the settings activity
        this.stopSelf(); //Stops the service
    }

    public void dismissSettingsDialog() //Function that dismisses any dialogs open on the settings activity
    {
        SharedPreferences sharedPref = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean settings_dialog = sharedPref.getBoolean("help", false);

        if (settings_dialog) { //If the dialog is open change the shared preference value
            editor = sharedPref.edit();
            editor.putBoolean("help", false);
            editor.apply(); //Apply the changes so the dialog is closed
        }

        dismissViewFragDialogs(); //Call the function to dismiss any open dialogs on the view fragments
    }

    public void dismissViewFragDialogs() //Function that dismisses any dialogs open on the view fragments
    {
        SharedPreferences sharedPref2 = this.getSharedPreferences("view_frag", Context.MODE_PRIVATE);
        boolean number_dialog = sharedPref2.getBoolean("number_pop_up",false);
        boolean area_dialog = sharedPref2.getBoolean("area_pop_up",false);
        boolean map_dialog = sharedPref2.getBoolean("map_pop_up",false);

        if (number_dialog){ //If the main dialog is open on ViewNumbersFragment change the shared preference value
            editor = sharedPref2.edit();
            editor.putBoolean("number_pop_up",false);
            editor.apply(); //Apply the changes so the dialog is closed
        }
        else if (area_dialog) { //If the dialog is open on ViewAreaCodesFragment change the shared preference value
            editor = sharedPref2.edit();
            editor.putBoolean("area_pop_up",false);
            editor.apply(); //Apply the changes so the dialog is closed
        }
        else if (map_dialog){ //If the map error dialog is open on ViewAreaCodesFragment change the shared preference value
            editor = sharedPref2.edit();
            editor.putBoolean("map_pop_up",false);
            editor.apply(); //Apply the changes so the dialog is closed
        }
    }
}
