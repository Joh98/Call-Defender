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
public class AddFragment extends Fragment {

    //Declare variables
    EditText block_input;
    CardView number_card_button;
    CardView area_card_button;
    int identifier;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) { //On creation

        View view = inflater.inflate(R.layout.fragment_add, null); //set the view

        //Set variables to represent the cards on the UI
        number_card_button = (CardView) view.findViewById(R.id.number_card_button);
        area_card_button = (CardView) view.findViewById(R.id.area_card_button);

        getActivity().setTitle(""); //Set the activity title to blank


        number_card_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //if card is pressed down by user

                    //Change appearance of the card
                    number_card_button.setCardElevation(35);
                    number_card_button.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                    addDialog("Enter Number to be Blocked",1); //call function so the user can input a number to be blocked via a dialog
                    return true;
                }
                else { //Else set appearance of the card back to it's original state
                    number_card_button.setCardElevation(15);
                    number_card_button.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    return false;
                }
            }
        });

        area_card_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //if card is pressed down by user

                    //Change appearance of the card
                    area_card_button.setCardElevation(35);
                    area_card_button.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                    addDialog("Enter Area Code to be Blocked",2); //call function so the user can input an area code to be blocked via a dialog
                    return true;
                }
                else { //Else set appearance of the card back to it's original state
                    area_card_button.setCardElevation(15);
                    area_card_button.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    return false;
                }
            }
        });

        return view; //Return the view
    }

    //Function for running an async DB task to determine if the number/area code the user has entered already exists on the 'blocked list'
    public void checkForNumber(String number) {
        new BackgroundNumberExistForAddingNumber(getActivity().getApplicationContext(), this).execute(number, String.valueOf(identifier));
    }

    //Function for the creation, displaying and parsing of blocked number/area code information
    public void addDialog(final String message, final int id) {

        //Set the prerequisites for the creation of the dialog presented to the user
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage(" ");
        builder.setCancelable(false);
        block_input = new EditText(getActivity());

        //Only allow numbers and other characters associated with phone numbers to be entered
        block_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        block_input.setRawInputType(Configuration.KEYBOARD_12KEY);

        block_input.setHint(message); //Set the hint to the required message (Area code or number)
        identifier = id; //Set id to identifier so the async DB task knows which DB query to run

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
                    checkForNumber(block_input.getText().toString());
                }
            }
        });

        builder.show(); //Show dialog
    }

    //Function used by the 'onPostExecute' of 'BackgroundNumberExistForAddingNumber'
    public void numAlreadyExists(Boolean flag) {

        if (flag) { //If the entered number/area code already exists in the DB alert the user via Toast
            Toast.makeText(getActivity(), "This number/area code is already on the blocked list. Please Try Again!",
                    Toast.LENGTH_SHORT).show();
        }
        else { //Else start an async task to insert the value entered by the user into the DB
           new BackgroundInsert(getActivity().getApplicationContext()).execute(block_input.getText().toString(), String.valueOf(identifier));
        }
    }
}
