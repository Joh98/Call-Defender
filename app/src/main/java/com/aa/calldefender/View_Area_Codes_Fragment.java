package com.aa.calldefender;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

public class View_Area_Codes_Fragment extends Fragment {

    ArrayList<String> num_list = new ArrayList<>();
    Cursor result;
    ListView a;
    DHelper data;
    Button button;
    android.support.v7.app.AlertDialog.Builder builder;
    String num_number,num_id;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_view_area_codes, null);
        data = new DHelper(getActivity().getApplicationContext());
        new BackgroundReadFromAreaCodeTable(getActivity().getApplicationContext(), this).execute("2");


        a = (ListView)view.findViewById(R.id.list);
        button = (Button)view.findViewById(R.id.button2);


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, new View_Numbers_Fragment())
                        .commit();
            }
        });

        a.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int column_number = result.getColumnIndex("area_code"); //select phone number column
                int column_id = result.getColumnIndex("ID_2"); //select ID column

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

        new BackgroundReadFromAreaCodeTable(getActivity().getApplicationContext(), this).execute("2");

    }

    private void runDelete()
    {
        new BackgroundDeleteFromAreaCodeTable(getActivity().getApplicationContext(), this).execute(num_id,"2");
    }


    public void alert()
    {
        builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Blocked Area Code: " + num_number);
        builder.setCancelable(false);
        builder.setNegativeButton("Unblock", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               runDelete();
            }
        });
        builder.setPositiveButton("Cancel", null);
        builder.show();
    }

    public void show_data(Cursor data)
    {
        num_list.clear();
        if(data.getCount() <= 0){
            Toast.makeText(getActivity(), "NO BLOCKED AREA CODES EXIST!",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            while (data.moveToNext()) {

                num_list.add(data.getString(1));
            }

        }

        ListAdapter l_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, num_list);
        a.setAdapter(l_adapter);

        result = data;
    }
}
