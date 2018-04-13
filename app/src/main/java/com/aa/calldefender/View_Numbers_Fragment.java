package com.aa.calldefender;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Jonathan on 24/03/2018.
 */

public class View_Numbers_Fragment extends Fragment {

    ArrayList<String> num_list = new ArrayList<>();
    Cursor result;
    ListView a;
    DHelper data;
    android.support.v7.app.AlertDialog.Builder builder;
    Button button;
    BackgroundDBTasks BackgroundTask = null;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_view_numbers, null);
        data = new DHelper(getActivity().getApplicationContext());

        result = data.getData(1);
        BackgroundTask = new BackgroundDBTasks(getActivity().getApplicationContext());

        a = (ListView)view.findViewById(R.id.list);
        button = (Button)view.findViewById(R.id.button_change);


        if(result.getCount() <= 0){
            Toast.makeText(getActivity(), "NO BLOCKED NUMBERS EXIST!",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            while (result.moveToNext()) {

                num_list.add(result.getString(1));
                ListAdapter l_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, num_list);
                a.setAdapter(l_adapter);

            }

        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, new View_Area_Codes_Fragment())
                        .commit();
            }
        });

        a.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int column_number = result.getColumnIndex("phone_number"); //select phone number column
                int column_id = result.getColumnIndex("ID"); //select ID column

                result.moveToPosition(i); //Set position of the row to that clicked on
                Integer row_id = result.getInt(column_id); //Get ID of the selected row
                String num_number = result.getString(column_number);
                String num_id = row_id.toString();
                alert(num_number, num_id);
            }
        });

        return view;
    }

    public void re_display()
    {
        FragmentTransaction refresh = getFragmentManager().beginTransaction();
        refresh.replace(R.id.content, new View_Numbers_Fragment());
        refresh.commit();
    }

    public void alert(final String num_number, final String num_id)
    {
        builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Blocked Number: " + num_number);
        builder.setCancelable(false);
        builder.setNegativeButton("Unblock", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                BackgroundTask.execute("delete", num_id, String.valueOf(1));
                //data.delete_from_db(num_id,1);
                re_display();
            }
        });
        builder.setNeutralButton("View on Map", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                boolean exist = data.area_code_exist(num_number, 2);
                if (exist)
                {
                    Intent intent = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
                    intent.putExtra("area_code", num_number);
                    startActivity(intent);
                }

                else
                {
                    builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    builder.setMessage("Sorry this area code is not in our database, we hope to have this information for you at a later date!");
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", null);
                    builder.show();

                }
            }
        });
        builder.setPositiveButton("Cancel", null);
        builder.show();





    }

}