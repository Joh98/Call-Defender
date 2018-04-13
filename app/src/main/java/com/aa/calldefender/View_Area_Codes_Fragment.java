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
    BackgroundDBTasks BackgroundTask = null;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_view_area_codes, null);
        data = new DHelper(getActivity().getApplicationContext());
        BackgroundTask = new BackgroundDBTasks(getActivity().getApplicationContext());

        result = data.getData(2);

        a = (ListView)view.findViewById(R.id.list);
        button = (Button)view.findViewById(R.id.button2);


        if(result.getCount() <= 0){
            Toast.makeText(getActivity(), "NO BLOCKED AREA CODES EXIST!",
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
                Integer row_number = result.getInt(column_number); //Get phone number of the selected row
                String num_id = row_id.toString();
                String num_number = row_number.toString();
                alert(num_number, num_id);
            }
        });

        return view;
    }

    public void re_display()
    {
        FragmentTransaction refresh = getFragmentManager().beginTransaction();
        refresh.replace(R.id.content, new View_Area_Codes_Fragment());
        refresh.commit();
    }

    public void alert(final String num_number, final String num_id)
    {
        builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Blocked Area Code: " + num_number);
        builder.setCancelable(false);
        builder.setNegativeButton("Unblock", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BackgroundTask.execute("delete", num_id, String.valueOf(2));
                re_display();
            }
        });
        builder.setPositiveButton("Cancel", null);
        builder.show();
    }

}
