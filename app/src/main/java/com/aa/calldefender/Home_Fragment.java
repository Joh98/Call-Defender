package com.aa.calldefender;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.telecom.Call;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;


public class Home_Fragment extends Fragment {

    Home enable_disable = new Home();
    CardView card_view;


    private GestureDetector mDetector;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    boolean on;
    boolean block_all;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);


        View view = inflater.inflate(R.layout.fragment_home, null);
        FrameLayout frameLayoutBalance = (FrameLayout) view.findViewById(R.id.home_frame);
        frameLayoutBalance.setBackgroundColor(Color.parseColor("#f9f9f9"));
        on = sharedPref.getBoolean("on", false);
        block_all = sharedPref.getBoolean("block_all", false);
        editor = sharedPref.edit();


        card_view = (CardView) view.findViewById(R.id.card_1);
        mDetector = new GestureDetector(new MyGestureListener());

        card_view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    card_view.setCardElevation(40);
                    card_view.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    card_view.setCardElevation(20);
                    card_view.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                }

                mDetector.onTouchEvent(event);


                return true;
            }
        });


        return view;
    }

    public void checkCallPermission() {

       requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG}, 1);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Insufficient permissions... call blocking DISABLED!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Permissions granted, you can now enable call blocking!",
                            Toast.LENGTH_LONG).show();
                }

    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {


            on = sharedPref.getBoolean("on", false);
            block_all = sharedPref.getBoolean("block_all", false);

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) { //If incorrect permissions ask user for pemissions

                checkCallPermission();
            }

            else if (!on) { // If call blocking isn't on, turn it on
                enable_disable.enableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", true);
                editor.apply();
                Toast.makeText(getActivity(), "ON!",
                        Toast.LENGTH_SHORT).show();
            } else { //Else turn call blocking off

                enable_disable.disableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", false);
                editor.putBoolean("block_all", false);
                editor.apply();

                Toast.makeText(getActivity(), "OFF!",
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

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
                Toast.makeText(getActivity(), "ALL!",
                        Toast.LENGTH_SHORT).show();
            }

            else if (on && !block_all) {

                enable_disable.enableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", true);
                editor.apply();
                editor.putBoolean("block_all", true);
                editor.apply();
                Toast.makeText(getActivity(), "ALL!",
                        Toast.LENGTH_SHORT).show();
            }

            else {
                enable_disable.disableBroadcastReceiver(getActivity().getApplicationContext());
                editor.putBoolean("on", false);
                editor.apply();
                editor.putBoolean("block_all", false);
                editor.apply();
                Toast.makeText(getActivity(), "OFF!",
                        Toast.LENGTH_SHORT).show();
            }


        }
    }
}


