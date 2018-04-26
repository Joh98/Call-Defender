package com.aa.calldefender;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import java.util.ArrayList;

public class View_Area_Codes_Fragment extends Fragment {

    ArrayList<String> num_list = new ArrayList<>();
    Cursor result;
    ListView a;
    DHelper data;
    Button button;
    android.support.v7.app.AlertDialog.Builder builder;
    String num_number,num_id;
    private Context context = null;
    SharedPreferences.Editor editor = null;
    SharedPreferences sharedPref;
    Boolean pop_up;
    TextView disclaimer;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_view_area_codes, null);
        new BackgroundReadFromAreaCodeTable(getActivity().getApplicationContext(), this).execute("2");
        context = getContext();
        a = (ListView)view.findViewById(R.id.list);
        button = (Button)view.findViewById(R.id.button2);
        sharedPref = getActivity().getSharedPreferences("view_frag", Context.MODE_PRIVATE);
        disclaimer = (TextView)view.findViewById(R.id.text_disclaimer);
        pop_up = sharedPref.getBoolean("area_pop_up", false);
        editor = sharedPref.edit();
        getActivity().setTitle("Blocked Area Codes");



        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editor.putInt("view_frag", 1);
                editor.apply();
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

        new BackgroundReadFromAreaCodeTable(context, this).execute("2");

    }

    private void runDelete()
    {
        new BackgroundDeleteFromAreaCodeTable(context, this).execute(num_id,"2");
    }


    public void alert()
    {
        builder = new android.support.v7.app.AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        editor.putBoolean("area_pop_up",true);
        editor.apply();
        builder.setMessage("Blocked Area Code: " + num_number);
        builder.setCancelable(false);
        builder.setNegativeButton("Unblock", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editor.putBoolean("area_pop_up",false);
                editor.apply();
                runDelete();

            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editor.putBoolean("area_pop_up",false);
                editor.apply();
                dialog.dismiss();
            }

        });
        builder.show();

        editor.putString("area_num",num_number);
        editor.apply();
        editor.putString("area_id",num_id);
        editor.apply();
    }

    public void show_data(Cursor data)
    {
        num_list.clear();
        if(data.getCount() <= 0){
            a.setVisibility(View.INVISIBLE);
            disclaimer.setVisibility(View.VISIBLE);
        }
        else {
            while (data.moveToNext()) {
                a.setVisibility(View.VISIBLE);
                disclaimer.setVisibility(View.INVISIBLE);

                num_list.add(data.getString(1));
            }

            ListAdapter l_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, num_list);
            a.setAdapter(l_adapter);

        }
        result = data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (pop_up)
        {
            sharedPref = getActivity().getSharedPreferences("view_frag", Context.MODE_PRIVATE);
            num_number = sharedPref.getString("area_num",null);
            num_id = sharedPref.getString("area_id",null);
            alert();
        }

    }

}
