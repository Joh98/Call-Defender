package com.aa.calldefender;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//Class for the Home Fragment (Where a user can turn call blocking on and off)
public class Home_Fragment extends Fragment {

    //Declare variables
    Home enable_disable = new Home();
    CardView card_view;
    private GestureDetector mDetector;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    boolean on;
    boolean block_all;
    int image;
    ImageView i_view;
    int blocked_calls_tally;
    TextView tally;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //Grab shared preferences
        sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        //Display the fragment
        View view = inflater.inflate(R.layout.fragment_home, null);
        i_view = view.findViewById(R.id.imageView3);
        getActivity().setTitle("");

        determine_image();
        FrameLayout frameLayoutBalance = (FrameLayout) view.findViewById(R.id.home_frame);

        //Save shared preferences data to variables and set up the editor
        on = sharedPref.getBoolean("on", false);
        block_all = sharedPref.getBoolean("block_all", false);
        editor = sharedPref.edit();
        blocked_calls_tally = sharedPref.getInt("tally", 0);
        tally = (TextView)view.findViewById(R.id.blocked_value);
        tally.setText(Integer.toString(blocked_calls_tally));
        //Set variable to represent the card on the UI and setup the gesture listener
        card_view = (CardView) view.findViewById(R.id.card_1);
        mDetector = new GestureDetector(new MyGestureListener());


        card_view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) { //If gesture is detected

                //Change card appearance when it is pressed down by a user and released
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    card_view.setCardElevation(15);
                    card_view.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    card_view.setCardElevation(7);
                    card_view.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                }

                mDetector.onTouchEvent(event); //Call the gesture detection class

                return true;
            }
        });

        start_receiver();
        return view; //Return the view
    }

    public void start_receiver()
    {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                update_tally();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.aa.calldefender.UPDATE_COUNT");
        getActivity().registerReceiver(receiver, filter);
    }

    public void update_tally()
    {
        blocked_calls_tally = sharedPref.getInt("tally", 0);
        tally.setText(Integer.toString(blocked_calls_tally));
        Log.d("Thread", "I'm alive");

    }

    public void determine_image()
    {
        image = sharedPref.getInt("image", 1);

        if (image == 1)
        {
            i_view.setImageResource(R.drawable.off);
        }

        else if (image == 2)
        {
            i_view.setImageResource(R.drawable.on);
        }

        else if (image == 3)
        {
            i_view.setImageResource(R.drawable.all);
        }

    }

    public void checkCallPermission() { //Function that checks for phone call permissions

        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG}, 1);

    }

    //Function that gets called as a result of running code within 'checkCallPermission()'.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//If user hasn't allowed call permissions warn the user
                    Toast.makeText(getActivity(), "Insufficient permissions... call blocking DISABLED!",
                            Toast.LENGTH_LONG).show();
                    editor.putBoolean("on", false);
                    editor.apply();
                    editor.putBoolean("block_all", false);
                    editor.apply();

                } else { //else tell the user they can now use call blocking
                    Toast.makeText(getActivity(), "Permissions granted, you can now enable call blocking!",
                            Toast.LENGTH_LONG).show();
                    editor.putBoolean("on", false);
                    editor.apply();
                    editor.putBoolean("block_all", false);
                    editor.apply();
                }

    }

    //Gesture detection class
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        //Function that will get called if the user has 'single tapped' the on screen card
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            //Save shared preference data to variables
            on = sharedPref.getBoolean("on", false);
            block_all = sharedPref.getBoolean("block_all", false);

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) { //If incorrect permissions ask user for permissions

                checkCallPermission();
            }

            else if (!on) { // If call blocking isn't on, turn it on
                enable_disable.enableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", true);
                editor.apply();
                editor.putInt("image",2);
                editor.apply();
                determine_image();


            } else { //Else turn call blocking off

                enable_disable.disableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", false);
                editor.putBoolean("block_all", false);
                editor.apply();
                editor.putInt("image",1);
                editor.apply();
                editor.putInt("tally", 0);
                editor.apply();
                tally.setText("0");
                determine_image();

            }

            return true;
        }

        //Function that will get called if the user has held down the on screen card
        @Override
        public void onLongPress(MotionEvent e) {

            //Save shared preference data to variables
            on = sharedPref.getBoolean("on", false);
            block_all = sharedPref.getBoolean("block_all", false);

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) { //If call blocking isn't on and there are no permissions

                checkCallPermission(); //Check for permissions
            }

            else if (!on) { //Else if there are already permissions and call blocking isn't on, block all numbers
                enable_disable.enableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", true);
                editor.apply();
                editor.putBoolean("block_all", true);
                editor.apply();
                editor.putInt("image",3);
                editor.apply();
                determine_image();
            }

            else if (on && !block_all) { // If call blocking isn't on and neither is the block all flag, turn on and set flag to true

                enable_disable.enableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", true);
                editor.apply();
                editor.putBoolean("block_all", true);
                editor.apply();
                editor.putInt("image",3);
                editor.apply();
                determine_image();
            }

            else { //Else turn call blocking off
                enable_disable.disableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", false);
                editor.apply();
                editor.putBoolean("block_all", false);
                editor.apply();
                editor.putInt("image",1);
                editor.apply();
                editor.putInt("tally", 0);
                editor.apply();
                tally.setText("0");
                determine_image();
            }

        }
    }
}


