package com.aa.calldefender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

//Class for the fragment where a user can view and delete phone numbers from their 'blocked' list, and start MapsActivity where appropriate
public class ViewNumbersFragment extends Fragment {

    //Declare variables
    ArrayList<String> num_list = new ArrayList<>();
    Cursor result;
    ListView phone_number_list;
    DHelper data;
    android.support.v7.app.AlertDialog.Builder builder;
    Button button;
    Boolean exist, pop_up, map_pop_up;
    String num_number,num_id;
    private Context context = null;
    SharedPreferences.Editor editor = null;
    SharedPreferences sharedPref;
    TextView disclaimer;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) { //On Creation

        View view =  inflater.inflate(R.layout.fragment_view_numbers, null); //Set View
        context = getContext(); //Set context
        new BackgroundReadFromNumberTable(context, this).execute("1"); //Run async DB task that will grab the blocked phone numbers

        //Set variables to represent the UI elements
        disclaimer = (TextView)view.findViewById(R.id.text_disclaimer);
        phone_number_list = (ListView)view.findViewById(R.id.list);
        button = (Button)view.findViewById(R.id.button_change_data);

        //Set up shared preferences for editing and save their values to variables
        sharedPref = getActivity().getSharedPreferences("view_frag", Context.MODE_PRIVATE);
        pop_up = sharedPref.getBoolean("number_pop_up", false); //Boolean value that determines if the dialog for removing the phone number should be displayed
        map_pop_up = sharedPref.getBoolean("map_pop_up", false); //Boolean value that determines if the dialog that informs the user that no map data exists should be displayed
        editor = sharedPref.edit();

        getActivity().setTitle("Blocked Phone Numbers"); //Set the activity title to 'Blocked Phone Numbers'

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //If the on screen button is pressed

                //Set the 'last loaded in fragment' to the ViewAreaCodesFragment
                editor.putInt("view_frag", 2);
                editor.apply();

                //Display the ViewNumbersFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, new ViewAreaCodesFragment())
                        .commit();
            }
        });

        phone_number_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //If a list item is clicked on

                int column_number = result.getColumnIndex("phone_number"); //select phone number column
                int column_id = result.getColumnIndex("ID"); //select ID column
                result.moveToPosition(i); //Set position of the row to that clicked on
                Integer row_id = result.getInt(column_id); //Get ID of the selected row
                num_number = result.getString(column_number);
                num_id = row_id.toString();
                alert(); //Call function to show dialog containing the clicked on data
            }
        });

        return view; //Return view
    }

    //Function called by the 'OnPostExecute' of BackGroundDeleteNumberTable that runs an async DB task that will re-read for the phone numbers and display them
    public void reDisplay() {

        new BackgroundReadFromNumberTable(context, this).execute("1");
    }

    private void runDelete() //Function that runs an async DB task that will delete the desired phone number from the DB
    {
        new BackgroundDeleteFromNumberTable(context, this).execute(num_id,"1");
    }

    private void checkAreaCodeExists() //Function that runs an async DB task that will check if the area code of the selected phone number exists with map data
    {
        new BackgroundNumberExistForMap(context, this).execute(num_number);
    }

    public void alert() { //Function that creates and displays a dialog where a user can unblock the selected phone number or attempt to view its map info

        builder = new android.support.v7.app.AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();

        //Set shared preference so dialog will remain visible when screen is orientated
        editor.putBoolean("number_pop_up",true);
        editor.apply();

        builder.setMessage("Blocked Number: " + num_number);
        builder.setCancelable(false);
        builder.setNegativeButton("Unblock", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { //If the unblock button is pressed

                //Set shared preference so dialog will be dismissed when screen is orientated
                editor.putBoolean("number_pop_up",false);
                editor.apply();

                runDelete(); //Call function that will delete the selected phone number from the database

            }
        });
        builder.setNeutralButton("View on Map", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { //If the view on map button is pressed

                //Set shared preference so dialog will be dismissed when screen is orientated
                editor.putBoolean("number_pop_up",false);
                editor.apply();

                checkAreaCodeExists(); //Call function that will query the DB to check if the area code of the selected phone number exists with map info=
            }

        });

        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { //If the cancel button is pressed

                //Set shared preference so dialog will be dismissed when screen is orientated
                editor.putBoolean("number_pop_up",false);
                editor.apply();

                dialog.dismiss(); //Close the dialog
            }

        });

        builder.show();

        //Set the selected phone number and its DB id of the selected list item so the data can be used if the screen orientation changes
        editor.putString("number_num",num_number);
        editor.putString("number_id",num_id);
        editor.apply();
    }

    public void showData(Cursor data) { //Function called by the 'OnPostExecute' of BackgroundReadFromNumberTable that will display the area codes on the 'blocked list'

        num_list.clear(); //Clear the list

        if(data.getCount() <= 0){ //If the cursor is empty (no entries) hide the listView and show the 'disclaimer' textView
            phone_number_list.setVisibility(View.INVISIBLE);
            disclaimer.setVisibility(View.VISIBLE);
        }
        else { //Else show the listView, hide the 'disclaimer' textView and save the DB query data to a list

            phone_number_list.setVisibility(View.VISIBLE);
            disclaimer.setVisibility(View.INVISIBLE);

            while (data.moveToNext()) {

                num_list.add(data.getString(1));
            }

            //Display the DB query results stored in the list to the screen
            ListAdapter l_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, num_list);
            phone_number_list.setAdapter(l_adapter);
        }

        result = data; //set the DB query result to a global variable so it can be used if the screen orientation changes
    }

    public void queryResult(Boolean result) //Function called by the 'OnPostExecute' of BackgroundNumberExistForMap that will determine if MapActivity should be started
    {
        exist = result; //Set variable to the result from the DB query

        if (exist) { //If the query returned true (the selected number's area code has map info)

            //Save the selected number to an intent, start MapActivity and pass the intent in
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("phone_number_for_map", num_number);
            startActivity(intent);
        }
        else { //Else
            //Set shared preference so dialog will remain visible when screen is orientated
            editor.putBoolean("map_pop_up",true);
            editor.apply();
            mapAlert(); //Call function to display the dialog informing the user that the area code of the number they selcted has no map info
        }
    }

    public void mapAlert(){  //Function that shows a dialog informing the user that the area code of the phone number they have selected has no map info currently

        builder = new android.support.v7.app.AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();

        builder.setMessage("Sorry there is no map data relating to this phone number in our database, we hope to have this information for you at a later date!");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { //If the cancel button is pressed

                //Set shared preference so dialog will be dismissed when screen is orientated
                editor.putBoolean("map_pop_up",false);
                editor.apply();

                dialog.dismiss(); //Close the dialog
            }

        });
        builder.show();
    }

    @Override
    public void onResume() { //When the fragment is resumed

        super.onResume();
        pop_up = sharedPref.getBoolean("number_pop_up", false);
        map_pop_up = sharedPref.getBoolean("map_pop_up", false);

        if (pop_up) { //If the main dialog should be open (i.e. will be called if the screen orientation changes)

            //Save the required data from the shared preferences to variables so it can be used in the dialog
            sharedPref = getActivity().getSharedPreferences("view_frag", Context.MODE_PRIVATE);
            num_number = sharedPref.getString("number_num",null);
            num_id = sharedPref.getString("number_id",null);

            alert(); //Call function to show the dialog
        }

        else if (map_pop_up)  //If the map error dialog should be open (i.e. will be called if the screen orientation changes)
        {
            mapAlert(); //Call function to show the dialog
        }
    }
}