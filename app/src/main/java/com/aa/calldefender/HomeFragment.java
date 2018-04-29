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
import android.support.annotation.NonNull;;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//Class for the Home Fragment (Where a user can turn call blocking on and off)
public class HomeFragment extends Fragment {

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
    BroadcastReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { //On creation

        View view = inflater.inflate(R.layout.fragment_home, null); //Set the view
        i_view = view.findViewById(R.id.shield_image); //Set variable to represent the imageView

        //Access and save shared preferences to variables
        sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        on = sharedPref.getBoolean("on", false); //Boolean value that determines if call blocking should be enabled
        block_all = sharedPref.getBoolean("block_all", false); //Boolean value that determines if all numbers should be blocked or just those in the DB
        blocked_calls_tally = sharedPref.getInt("tally", 0); //Int value that represents the number of 'calls blocked this session'

        editor = sharedPref.edit(); //Set up shared preferences editor

        getActivity().setTitle(""); //Set the activity title to blank
        determineImage(); //Call function to display the correct 'shield' image

        //Set variables to represent the card on the UI and the textView. Setup the gesture listener
        card_view = (CardView) view.findViewById(R.id.on_off);
        tally = (TextView)view.findViewById(R.id.blocked_value);
        mDetector = new GestureDetector(new MyGestureListener());

        //Set the number of 'calls blocked this session' (from shared preference) to the textView on screen
        tally.setText(Integer.toString(blocked_calls_tally));

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

        startReceiver(); //Call the function that starts a broadcast receiver
        return view; //Return the view
    }

    public void startReceiver() //Function that starts a receiver that listens for broadcasts
    {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { //When a broadcast is received (i.e. a call has been blocked)
                updateTally(); //Call the function to update the number of 'calls blocked this session'
            }
        };

        //Register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.aa.calldefender.UPDATE_COUNT");
        getActivity().registerReceiver(receiver, filter);
    }

    public void updateTally() //Function that sets the textView on screen to the number stored in blocked_calls_tally
    {
        blocked_calls_tally = sharedPref.getInt("tally", 0);
        tally.setText(Integer.toString(blocked_calls_tally));
    }

    public void determineImage() //Function that determines which image should be displayed on screen
    {
        image = sharedPref.getInt("image", 1);

        if (image == 1) //1 represents the OFF image
        {
            i_view.setImageResource(R.drawable.off);
        }
        else if (image == 2) //2 represents the ON image
        {
            i_view.setImageResource(R.drawable.on);
        }
        else if (image == 3) //3 represents the ALL image
        {
            i_view.setImageResource(R.drawable.all);
        }
    }

    public void checkCallPermission() { //Function that asks user for phone call permissions

        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG}, 1);

    }

    //Function that gets called as a result of running code within 'checkCallPermission'
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//If user hasn't allowed call permissions warn the user
                    Toast.makeText(getActivity(), "Insufficient permissions... call blocking DISABLED!",
                            Toast.LENGTH_LONG).show();
                    //Set call blocking shared preferences to false
                    editor.putBoolean("on", false);
                    editor.putBoolean("block_all", false);
                    editor.apply();

                } else { //Else tell the user they can now use call blocking
                    Toast.makeText(getActivity(), "Permissions granted, you can now enable call blocking!",
                            Toast.LENGTH_LONG).show();
                    //Set call blocking shared preferences to false
                    editor.putBoolean("on", false);
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

            else if (!on) { // If call blocking isn't on, turn it on and change image
                enable_disable.enableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", true);
                editor.putInt("image",2);
                editor.apply();
                determineImage();

            } else { //Else turn call blocking off, set 'calls blocked this session' to 0 and change image

                enable_disable.disableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", false);
                editor.putBoolean("block_all", false);
                editor.putInt("image",1);
                editor.putInt("tally", 0);
                editor.apply();
                tally.setText("0");
                determineImage();
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

            } else if (!on) { //Else if there are already permissions and call blocking isn't on, block all numbers and change image
                enable_disable.enableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", true);
                editor.putBoolean("block_all", true);
                editor.putInt("image",3);
                editor.apply();
                determineImage();

            } else if (on && !block_all) { // If call blocking is on and the block all flag isn't, block all calls and change inage

                enable_disable.enableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", true);
                editor.putBoolean("block_all", true);
                editor.putInt("image",3);
                editor.apply();
                determineImage();

            } else { //Else turn call blocking off, set 'calls blocked this session' to 0 and chane image
                enable_disable.disableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", false);
                editor.putBoolean("block_all", false);
                editor.putInt("image",1);
                editor.putInt("tally", 0);
                tally.setText("0");
                determineImage();
            }

        }
    }

    @Override
    public void onDestroy() { //On destroy unregister receiver
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}


