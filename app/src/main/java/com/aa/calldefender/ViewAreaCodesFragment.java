package com.aa.calldefender;

import android.content.Context;
import android.content.DialogInterface;
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

//Class for the fragment where a user can view and delete area codes from their 'blocked' list
public class ViewAreaCodesFragment extends Fragment {

    //Declare variables
    ArrayList<String> num_list = new ArrayList<>();
    Cursor result;
    ListView area_code_list;
    DHelper data;
    Button button;
    android.support.v7.app.AlertDialog.Builder builder;
    String num_number,num_id;
    private Context context = null;
    SharedPreferences.Editor editor = null;
    SharedPreferences sharedPref;
    Boolean pop_up;
    TextView disclaimer;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) { //On Creation

        View view =  inflater.inflate(R.layout.fragment_view_area_codes, null); //Set view
        new BackgroundReadFromAreaCodeTable(getActivity().getApplicationContext(), this).execute("2");  //Run async DB task that will grab the blocked area codes
        context = getContext(); //Set context

        //Set variables to represent the UI elements
        area_code_list = (ListView)view.findViewById(R.id.list);
        button = (Button)view.findViewById(R.id.button_change_data);
        disclaimer = (TextView)view.findViewById(R.id.text_disclaimer);

        //Set up shared preferences for editing and save their values to variables
        sharedPref = getActivity().getSharedPreferences("view_frag", Context.MODE_PRIVATE);
        pop_up = sharedPref.getBoolean("area_pop_up", false); //Boolean value that determines if the dialog for removing the area code should be displayed
        editor = sharedPref.edit();

        getActivity().setTitle("Blocked Area Codes"); //Set the activity title to 'Blocked Area Codes'


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //If the on screen button is pressed

                //Set the 'last loaded in fragment' to the ViewNumbersFragment
                editor.putInt("view_frag", 1);
                editor.apply();

                //Display the ViewNumbersFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, new ViewNumbersFragment())
                        .commit();
            }
        });

        area_code_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { //If a list item is clicked on

                int column_number = result.getColumnIndex("area_code"); //select phone number column
                int column_id = result.getColumnIndex("ID_2"); //select ID column

                result.moveToPosition(i); //Set position of the row to that clicked on
                Integer row_id = result.getInt(column_id); //Get ID of the selected row
                num_number = result.getString(column_number);
                num_id = row_id.toString();
                alert(); //Call function to show dialog containing the clicked on data
            }
        });

        return view; //Return view
    }

    //Function called by the 'OnPostExecute' of BackGroundDeleteFromAreaCodeTable that runs an async DB task that will re-read for the area codes and display them
    public void reDisplay(){

        new BackgroundReadFromAreaCodeTable(context, this).execute("2");
    }

    private void runDelete() { //Function that runs an async DB task that will delete the desired area code from the DB

        new BackgroundDeleteFromAreaCodeTable(context, this).execute(num_id,"2");
    }

    public void alert() { //Function that creates and displays a dialog where a user can unblock the selected area code

        builder = new android.support.v7.app.AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();

        //Set shared preference so dialog will remain visible when screen is orientated
        editor.putBoolean("area_pop_up",true);
        editor.apply();

        builder.setMessage("Blocked Area Code: " + num_number);
        builder.setCancelable(false);
        builder.setNegativeButton("Unblock", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { //If the unblock button is pressed

                //Set shared preference so dialog will be dismissed when screen is orientated
                editor.putBoolean("area_pop_up",false);
                editor.apply();

                runDelete(); //Call function that will delete the selected area code from the database

            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { //If the cancel button is pressed

                //Set shared preference so dialog will be dismissed when screen is orientated
                editor.putBoolean("area_pop_up",false);
                editor.apply();

                dialog.dismiss(); //Close the dialog
            }

        });
        builder.show();

        //Set the area code and the DB ID of the selected list item so the data can be used if the screen orientation changes
        editor.putString("area_num",num_number);
        editor.putString("area_id",num_id);
        editor.apply();
    }

    public void showData(Cursor data) //Function called by the 'OnPostExecute' of BackgroundReadFromAreaCodeTable that will display the area codes on the 'blocked list'
    {
        num_list.clear(); //Clear the list

        if(data.getCount() <= 0){ //If the cursor is empty (no entries) hide the listView and show the 'disclaimer' textView
            area_code_list.setVisibility(View.INVISIBLE);
            disclaimer.setVisibility(View.VISIBLE);
        }
        else { //Else show the listView, hide the 'disclaimer' textView and save the DB query data to a list
            area_code_list.setVisibility(View.VISIBLE);
            disclaimer.setVisibility(View.INVISIBLE);

            while (data.moveToNext()) {
                num_list.add(data.getString(1));
            }

            //Display the DB query results stored in the list to the screen
            ListAdapter l_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, num_list);
            area_code_list.setAdapter(l_adapter);

        }
        result = data; //set the DB query result to a global variable so it can be used if the screen orientation changes
    }

    @Override
    public void onResume() //When the fragment is resumed
    {
        super.onResume();
        pop_up = sharedPref.getBoolean("area_pop_up", false);

        if (pop_up) { //If the dialog should be open (i.e. will be called if the screen orientation changes)

            //Save the required data from the shared preferences to variables so it can be used in the dialog
            sharedPref = getActivity().getSharedPreferences("view_frag", Context.MODE_PRIVATE);
            num_number = sharedPref.getString("area_num",null);
            num_id = sharedPref.getString("area_id",null);

            alert(); //Call function to show the dialog
        }
    }
}
