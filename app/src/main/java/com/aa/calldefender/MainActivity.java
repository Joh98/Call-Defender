package com.aa.calldefender;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    public void disableBroadcastReceiver(Context context){
        ComponentName receiver = new ComponentName(context, CallInterceptor.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disableBroadcastReceiver(this.getApplicationContext());

        final Button button_instructions = (Button) findViewById(R.id.button_instructions);
        final Button button_exit = (Button) findViewById(R.id.button_exit);
        final Button button_start = (Button) findViewById(R.id.button_start);


        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG, Manifest.permission. SEND_SMS }, 4);

        button_instructions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//              AlertDialog.Builder builder_1 = new AlertDialog.Builder(MainActivity.this);
//              builder_1.setTitle("INSTRUCTIONS");
//              builder_1.setMessage("Call Defender can be used to:" + System.lineSeparator() + System.lineSeparator() +
//                      "- Block all phone numbers" + System.lineSeparator() +
//                      "- Block phone numbers by area code" + System.lineSeparator() +
//                      "- Unblock phone numbers" + System.lineSeparator() +
//                      "- View statistics regarding the blocked numbers" + System.lineSeparator()
//              ).setCancelable(true);
//              AlertDialog instructions = builder_1.create();
//              instructions.show();

                Intent intent = new Intent(v.getContext(), Settings.class);
                startActivity(intent);

           }
       });

        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Home.class);
                startActivity(intent);
            }
        });


    }

}
