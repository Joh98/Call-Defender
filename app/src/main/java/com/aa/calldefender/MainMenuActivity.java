package com.aa.calldefender;
import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


//Class for the main menu activity (the 1st screen the user sees)
public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { //On creation

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Set the view

        //Start service that detects when the app has been removed via 'All Apps' view
        Intent i = new Intent(this, RemovedFromAllApps.class);
        startService(i);

        //Set variables to represent UI elements
        final Button button_settings = (Button) findViewById(R.id.button_settings);
        final Button button_exit = (Button) findViewById(R.id.button_exit);
        final Button button_start = (Button) findViewById(R.id.button_start);

        this.setTitle(""); //Set the activity title to blank

        //Request user for permissions if they haven't already been granted
        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG, Manifest.permission. SEND_SMS }, 4);

        button_settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //If settings button is clicked start 'Settings' activity
                Intent intent = new Intent(v.getContext(), Settings.class);
                startActivity(intent);

           }
       });

        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //If exit button is clicked exit the application
                finish();
                System.exit(0);
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //If start button is clicked start 'Home' activity
                Intent intent = new Intent(v.getContext(), Home.class);
                startActivity(intent);
            }
        });
    }
}
