package com.aa.calldefender;

import android.Manifest;;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

//Class for the settings activity
public class Settings extends AppCompatActivity {

    //Declare variables
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
    protected void onCreate(Bundle savedInstanceState) { //On creation

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); //Set view

        //Set variables to represent UI elements
        switchSMS = (Switch)this.findViewById(R.id.switch_sms);
        switchCallDel = (Switch)this.findViewById(R.id.switch_call_del);
        button_help = (ImageButton)findViewById(R.id.button_help);

        context = this; //Set context

        this.setTitle("Settings"); //Set the activity title to 'Settings'

        //Set up shared preferences for editing and save their values to variables
        sharedPref = this.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        sms_pref = sharedPref.getBoolean("sms", false); //Boolean value that determines if an sms should be sent to blocked numbers if they call
        call_dell_pref = sharedPref.getBoolean("call_del", false); //Boolean value that determines if the call log entry of blocked calls should be deleted
        help = sharedPref.getBoolean("help",false); //Boolean value that determines if the 'help' dialog should be displayed

        //Set the switches on the UI to represent the values of sms_pref and call_dell_pref
        switchSMS.setChecked(sms_pref);
        switchCallDel.setChecked(call_dell_pref);

        if(help) { //If 'help' dialog should be displayed
            showHelp(); //Call function to show the dialog
        }

        button_help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //If 'help' button is pressed

            showHelp(); //Call function to show the 'help' dialog

            //Set the 'help' shared preference to true so if orientation changes the dialog is still displayed
            editor.putBoolean("help", true);
            editor.apply();
            }
        });

        switchSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //If the sms switch is toggled
                sms_pref = sharedPref.getBoolean("sms", false); //Grab sms shared preference

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) { //If sms permissions are granted

                    if (!sms_pref) { //If the switch is off

                        //Set sms sending to true
                        editor.putBoolean("sms", true);
                        editor.apply();

                        switchSMS.setChecked(true);//Toggle switch to the 'on' position
                        showWarning(); //Call function to display Toast warning message

                    } else { //Else set sms sending to false and toggle switch to the 'off' position
                        editor.putBoolean("sms", false);
                        editor.apply();
                        switchSMS.setChecked(false);
                    }

                } else if (buttonView.isPressed()) { //Else if sms permissions haven't been granted and the user has toggled the switch
                    checkSMSPermission(); //Call function to ask for sms permissions
                }
            }
         });

        switchCallDel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //If the call deletion switch is toggled
                call_dell_pref = sharedPref.getBoolean("call_del", false); //Grab call deletion shared preference

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) { //If call permissions are granted

                    if (!call_dell_pref) { //If the switch is off

                        //Set call log deletion to true
                        editor.putBoolean("call_del", true);
                        editor.apply();

                        switchCallDel.setChecked(true); //Toggle switch to the 'on' position

                    } else { //Else set call log deletion to false and toggle switch to the 'off' position
                        editor.putBoolean("call_del", false);
                        editor.apply();
                        switchCallDel.setChecked(false);
                    }
                } else if (buttonView.isPressed()) { //Else if call permissions permissions haven't been granted and the user has toggled the switch
                    checkCallPermission(); //Call function to ask for call permissions
                }
            }
        });
    }

    public void checkSMSPermission() { //Function that asks user for sms permissions

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission. SEND_SMS}, 1);

    }

    public void checkCallPermission() { //Function that asks user for call permissions

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG}, 2);
        }

    //Function that gets called as a result of running code within 'checkSMSPermission' or 'checkCallPermission'
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: //Case sms permission
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) { //If user hasn't granted permissions

                    //Set sms sending to false and toggle its switch to the 'off' position
                    editor.putBoolean("sms", false);
                    editor.apply();
                    switchSMS.setChecked(false);
                    return;

                }
                else { //Else (permissions granted)

                    //Set sms sending to true and toggle its switch to the 'on' position
                    editor.putBoolean("sms", true);
                    switchSMS.setChecked(true);
                    editor.apply();

                    showWarning(); //Call function to display Toast warning message
                    return;
                }


            case 2: //Case call permissions
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) { //If user hasn't granted permissions

                    //Set call log deletion to false and toggle its switch to the 'off' position
                    editor.putBoolean("call_del", false);
                    switchCallDel.setChecked(false);
                    editor.apply();
                }
                else //Else (permissions granted)
                {
                    //Set call log deletion to true and toggle its switch to the 'on' position
                    editor.putBoolean("call_del", true);
                    switchCallDel.setChecked(true);
                    editor.apply();
                }
        }
    }

    public void showWarning(){ //Function that displays a Toast warning if the user turns on sms sending

        Toast.makeText(this, "WARNING: using this feature may charge you as it allows Call Defender to send SMS's!",
                Toast.LENGTH_LONG).show();
    }

    public void showHelp() { //Function that builds and displays the 'help' dialog using a custom layout

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