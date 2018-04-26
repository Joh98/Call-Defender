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
import android.widget.EditText;
import android.widget.Toast;


//Class for the Add Fragment (Where a user can add phone numbers and area codes to be blocked)
public class Add_Fragment extends Fragment {

    //Declare variables
    EditText block_input;
    CardView card_view;
    CardView card_view_2;
    int identifier;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add, null); //set the view

        //Set variables to represent the cards on the UI
        card_view = (CardView) view.findViewById(R.id.card_1);
        card_view_2 = (CardView) view.findViewById(R.id.card_2);
        getActivity().setTitle("");


        card_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //if card is pressed down by user

                    //Change appearance of the card
                    card_view.setCardElevation(35);
                    card_view.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                    add_dialog("Enter Number to be Blocked",1); //call function so the user can input a number to be blocked via a dialog

                    return true;
                } else { //else set appearance of the card back to it's original state
                    card_view.setCardElevation(20);
                    card_view.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

                    return false;

                }
            }
        });

        card_view_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //if card is pressed down by user

                    //Change appearance of the card
                    card_view_2.setCardElevation(35);
                    card_view_2.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                    add_dialog("Enter Area Code to be Blocked",2); //call function so the user can input an area code to be blocked via a dialog


                    return true;
                } else { //else set appearance of the card back to it's original state
                    card_view_2.setCardElevation(20);
                    card_view_2.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

                    return false;

                }
            }
        });

        return view; //return the view
    }

    //Function for running an async DB query to determine if the number/area code the user has entered already exists on the "blocked list"
    public void check_for_number(String number)
    {
        new BackgroundNumberExistForAddingNumber(getActivity().getApplicationContext(), this).execute(number, String.valueOf(identifier));
    }

    //Function for the creation, displaying and parsing of blocked number/area code information
    public void add_dialog(final String message, final int id)
    {
        //Set the prerequisites for the creation of the dialog presented to the user
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage(" ");
        builder.setCancelable(false);
        block_input = new EditText(getActivity());

        //Only allow numbers and other characters associated with phone numbers to be entered
        block_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        block_input.setRawInputType(Configuration.KEYBOARD_12KEY);

        block_input.setHint(message); //Set the hint to the required message (Area code or number)
        identifier = id; //set id to identifier so the async DB task knows which DB query to run

        //Set the dialog's view and buttons
        builder.setView(block_input);
        builder.setPositiveButton("Cancel", null);
        builder.setNegativeButton("Add", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                if (block_input.getText().toString().equals("")) { //if the user tries to submit no value then alert user via toast
                    Toast.makeText(getActivity(), "No data entered. Please Try Again!",
                            Toast.LENGTH_SHORT).show();
                }

                else { //else call the function to start the async DB task to determine if the entered value already exists
                    check_for_number(block_input.getText().toString());
                }
            }
        });

        builder.show(); //Show dialog

    }

    //Function used by the "post execute()" of 'BackgroundNumberExistForAddingNumber'
    public void num_already_exists(Boolean flag) {

        if (flag) { //If the entered number/area code already exists in the DB alert the user
            Toast.makeText(getActivity(), "This number/area code is already on the blocked list. Please Try Again!",
                    Toast.LENGTH_SHORT).show();
        }

        else { //else start an async task to insert the value entered by the user into the DB
           new BackgroundInsert(getActivity().getApplicationContext()).execute(block_input.getText().toString(), String.valueOf(identifier));
        }
    }
}
