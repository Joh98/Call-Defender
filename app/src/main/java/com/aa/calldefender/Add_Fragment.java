package com.aa.calldefender;


import android.content.DialogInterface;
import android.content.res.Configuration;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class Add_Fragment extends Fragment {

    EditText block_input;
    CardView card_view;
    CardView card_view_2;
    DHelper data;
    int identifier;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add, null);
        data = new DHelper(getActivity().getApplicationContext());

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);

        card_view = (CardView) view.findViewById(R.id.card_1);
        card_view_2 = (CardView) view.findViewById(R.id.card_2);


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
                    card_view_2.setCardElevation(35);
                    card_view_2.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                    add_dialog("Enter Area Code to be Blocked",2);

                    return true;
                } else {
                    card_view_2.setCardElevation(20);
                    card_view_2.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

                    return false;

                }
            }
        });

        return view;
    }

    public void check_for_number(String number)
    {
        new BackgroundNumberExistForAddingNumber(getActivity().getApplicationContext(), this).execute(number, String.valueOf(identifier));
    }

    public void add_dialog(final String message, final int id)
    {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage(" ");
        builder.setCancelable(false);
        block_input = new EditText(getActivity());
        block_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        block_input.setRawInputType(Configuration.KEYBOARD_12KEY);
        block_input.setHint(message);
        identifier = id;

        builder.setView(block_input);
        builder.setPositiveButton("Cancel", null);
        builder.setNegativeButton("Add", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (block_input.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "No data entered. Please Try Again!",
                            Toast.LENGTH_SHORT).show();
                }



                  check_for_number(block_input.getText().toString());


            }
        });

        builder.show();

    }

    public void num_already_exists(Boolean flag) {
        if (flag) {
            Toast.makeText(getActivity(), "This number/area code is already on the blocked list. Please Try Again!",
                    Toast.LENGTH_SHORT).show();
        }

        else {
           new BackgroundInsert(getActivity().getApplicationContext()).execute(block_input.getText().toString(), String.valueOf(identifier));
        }
    }
}

