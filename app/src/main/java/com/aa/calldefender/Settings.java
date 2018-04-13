package com.aa.calldefender;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    SharedPreferences.Editor editor = null;
    Switch switchSMS;
    Switch switchCallDel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final SharedPreferences sharedPref = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        switchSMS = (Switch)this.findViewById(R.id.switch_sms);
        switchCallDel = (Switch)this.findViewById(R.id.switch_call_del);

        switchSMS.setChecked(sharedPref.getBoolean("sms", false));
        switchCallDel.setChecked(sharedPref.getBoolean("call_del", false));


        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {

                    checkSMSPermission();

                }

                else {
                    editor.putBoolean("sms", false);
                    editor.apply();
                }
            }
         });

        switchCallDel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    checkCallPermission();

                }

                else {
                    editor.putBoolean("call_del", false);
                    editor.apply();
                }
            }
        });

    }
    public void checkSMSPermission() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission. SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission. SEND_SMS}, 1);
        }
    }

    public void checkCallPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG}, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    editor.putBoolean("sms", false);
                    editor.apply();
                    switchSMS.setChecked(false);
                }

                else
                {
                    editor.putBoolean("sms", true);
                    editor.apply();
                }

            case 2:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    editor.putBoolean("call_del", false);
                    editor.apply();
                    switchCallDel.setChecked(false);
                }

                else
                {
                    editor.putBoolean("call_del", true);
                    editor.apply();
                }

        };
    }


}
