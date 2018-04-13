package com.aa.calldefender;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


public class Add_Fragment extends Fragment {

    EditText block_input;
    CardView card_view;
    CardView card_view_2;
    DHelper data;
    BackgroundDBTasks BackgroundTask = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add, null);
        data = new DHelper(getActivity().getApplicationContext());

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);

        card_view = (CardView) view.findViewById(R.id.card_1);
        card_view_2 = (CardView) view.findViewById(R.id.card_2);
        BackgroundTask = new BackgroundDBTasks(getActivity().getApplicationContext());

        card_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    card_view.setCardElevation(35);
                    card_view.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                    add_dialog("Enter Number to be Blocked",1);

                    return true;
                } else {
                    card_view.setCardElevation(20);
                    card_view.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

                    return false;

                }
            }
        });

        card_view_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    card_view.setCardElevation(35);
                    card_view.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                    add_dialog("Enter Area Code to be Blocked",2);

                    return true;
                } else {
                    card_view.setCardElevation(20);
                    card_view.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

                    return false;

                }
            }
        });

        return view;
    }

    public void add_dialog(final String message, final int identifier)
    {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage(" ");
        builder.setCancelable(false);
        block_input = new EditText(getActivity());
        block_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        block_input.setRawInputType(Configuration.KEYBOARD_12KEY);
        block_input.setHint(message);

        builder.setView(block_input);
        builder.setPositiveButton("Cancel", null);
        builder.setNegativeButton("Add", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (block_input.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "No data entered. Please Try Again!",
                            Toast.LENGTH_SHORT).show();
                            add_dialog(message, identifier);
                }

               else {

                   String var = block_input.getText().toString();
                    BackgroundTask.execute("insert", var, String.valueOf(identifier));
                   // data.insert_to_db(block_input.getText().toString(), identifier)
                }
            }
        });

        builder.show();

    }
}

