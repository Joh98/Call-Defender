package com.aa.calldefender;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    SharedPreferences.Editor editor = null;
    Switch switchSMS;
    Switch switchCallDel;
    Context context;
    Boolean sms_pref;
    Boolean help;
    Boolean call_dell_pref;
    SharedPreferences sharedPref = null;
    ImageButton button_help;
    android.support.v7.app.AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPref = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        switchSMS = (Switch)this.findViewById(R.id.switch_sms);
        switchCallDel = (Switch)this.findViewById(R.id.switch_call_del);
        context = this;
        this.setTitle("Settings");


        sms_pref = sharedPref.getBoolean("sms", false);
        call_dell_pref = sharedPref.getBoolean("call_del", false);
        help = sharedPref.getBoolean("help",false);
        switchSMS.setChecked(sms_pref);
        switchCallDel.setChecked(call_dell_pref);
        button_help = (ImageButton)findViewById(R.id.button_help);

        if(help)
        {
            show_help();
        }

        button_help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            show_help();
            editor.putBoolean("help", true);
            editor.apply();
            }
        });


        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sms_pref = sharedPref.getBoolean("sms", false);

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                    if (!sms_pref) {
                        editor.putBoolean("sms", true);
                        editor.apply();
                        switchSMS.setChecked(true);
                        show_warning();

                    } else {
                        editor.putBoolean("sms", false);
                        editor.apply();
                        switchSMS.setChecked(false);
                    }

                } else if (buttonView.isPressed()) {
                    checkSMSPermission();
                }
            }
         });

        switchCallDel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                call_dell_pref = sharedPref.getBoolean("call_del", false);
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                    if (!call_dell_pref) {
                        editor.putBoolean("call_del", true);
                        editor.apply();
                        switchCallDel.setChecked(true);

                    } else {
                        editor.putBoolean("call_del", false);
                        editor.apply();
                        switchCallDel.setChecked(false);
                    }
                } else if (buttonView.isPressed()) {
                    checkCallPermission();
                }
            }
        });
    }

    public void checkSMSPermission() {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission. SEND_SMS}, 1);

    }

    public void checkCallPermission() {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG}, 2);
        }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                    editor.putBoolean("sms", false);
                    editor.apply();
                    switchSMS.setChecked(false);
                    return;

                }

                else
                {
                    editor.putBoolean("sms", true);
                    switchSMS.setChecked(true);
                    editor.apply();
                   show_warning();
                    return;
                }


            case 2:
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                {
                    editor.putBoolean("call_del", false);
                    switchCallDel.setChecked(false);
                    editor.apply();
                }

                else
                {
                    editor.putBoolean("call_del", true);
                    switchCallDel.setChecked(true);
                    editor.apply();
                }

        }
    }

    public void show_warning(){
        Toast.makeText(this, "WARNING: using this feature may charge you as it allows Call Defender to send SMS's!",
                Toast.LENGTH_LONG).show();}

    public void show_help()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_help, null);
        builder = new android.support.v7.app.AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        builder.setView(alertLayout);
        builder.setCancelable(false);
        builder.setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editor.putBoolean("help", false);
                editor.apply();
                dialog.dismiss();

            }
        });
        builder.show();
    }
}
