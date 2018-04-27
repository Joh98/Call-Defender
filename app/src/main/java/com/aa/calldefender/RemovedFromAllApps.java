package com.aa.calldefender;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class RemovedFromAllApps extends Service {

    SharedPreferences sharedPref;
    SharedPreferences sharedPref2;
    SharedPreferences.Editor editor = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        dismiss_settings_dialog();
        this.stopSelf();
    }

    public void dismiss_settings_dialog()
    {
        sharedPref = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean("help", false);
        editor.apply();
        dismiss_view_frag_dialogs();

    }

    public void dismiss_view_frag_dialogs()
    {
        sharedPref2 = this.getSharedPreferences("view_frag", Context.MODE_PRIVATE);
        editor = sharedPref2.edit();
        editor.putBoolean("number_pop_up",false);
        editor.apply();
        editor.putBoolean("area_pop_up",false);
        editor.apply();
    }
}


