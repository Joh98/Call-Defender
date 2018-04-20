package com.aa.calldefender;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    Boolean exist;
    String num_number,num_id;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_view_numbers, null);
        data = new DHelper(getActivity().getApplicationContext());

        new BackgroundReadFromNumberTable(getActivity().getApplicationContext(), this).execute("1");

        a = (ListView)view.findViewById(R.id.list);
        button = (Button)view.findViewById(R.id.button_change);

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
                num_number = result.getString(column_number);
                 num_id = row_id.toString();
                alert();
            }
        });

        return view;
    }

    public void re_display()
    {

        new BackgroundReadFromNumberTable(getActivity().getApplicationContext(), this).execute("1");

    }

    private void runDelete()
    {
        new BackgroundDeleteFromNumberTable(getActivity().getApplicationContext(), this).execute(num_id,"1");
    }

    private void check_area_code_exists()
    {
        new BackgroundNumberExistForMap(getActivity().getApplicationContext(), this).execute(num_number);
    }


    public void alert()
    {
        builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Blocked Number: " + num_number);
        builder.setCancelable(false);
        builder.setNegativeButton("Unblock", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                runDelete();

            }
        });
     builder.setNeutralButton("View on Map", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                check_area_code_exists();

            }

      });

        builder.setPositiveButton("Cancel", null);
        builder.show();


    }

    public void show_data(Cursor data)
    {
        num_list.clear();
        if(data.getCount() <= 0){
            Toast.makeText(getActivity(), "NO BLOCKED NUMBERS EXIST!",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            while (data.moveToNext()) {

                num_list.add(data.getString(1));
            }

        }

//        Activity act = getActivity();

        ListAdapter l_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, num_list);
        a.setAdapter(l_adapter);

        result = data;
    }

    public void query_result(Boolean result)
    {
        exist = result;

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
}
