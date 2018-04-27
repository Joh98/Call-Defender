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
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class View_Numbers_Fragment extends Fragment {

    ArrayList<String> num_list = new ArrayList<>();
    Cursor result;
    ListView a;
    DHelper data;
    android.support.v7.app.AlertDialog.Builder builder;
    Button button;
    Boolean exist, pop_up;
    String num_number,num_id;
    private Context context = null;
    SharedPreferences.Editor editor = null;
    SharedPreferences sharedPref;
    TextView disclaimer;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_view_numbers, null);
        context = getContext();
        new BackgroundReadFromNumberTable(context, this).execute("1");
        sharedPref = getActivity().getSharedPreferences("view_frag", Context.MODE_PRIVATE);
        pop_up = sharedPref.getBoolean("number_pop_up", false);
        editor = sharedPref.edit();
        getActivity().setTitle("Blocked Phone Numbers");

        disclaimer = (TextView)view.findViewById(R.id.text_disclaimer);
        a = (ListView)view.findViewById(R.id.list);
        button = (Button)view.findViewById(R.id.button_change);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                editor.putInt("view_frag", 2);
                editor.apply();
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

        new BackgroundReadFromNumberTable(context, this).execute("1");

    }

    private void runDelete()
    {
        new BackgroundDeleteFromNumberTable(context, this).execute(num_id,"1");
    }

    private void check_area_code_exists()
    {
        new BackgroundNumberExistForMap(context, this).execute(num_number);
    }


    public void alert()
    {
        builder = new android.support.v7.app.AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        editor.putBoolean("number_pop_up",true);
        editor.apply();
        builder.setMessage("Blocked Number: " + num_number);
        builder.setCancelable(false);
        builder.setNegativeButton("Unblock", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editor.putBoolean("number_pop_up",false);
                editor.apply();
                runDelete();

            }
        });
     builder.setNeutralButton("View on Map", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editor.putBoolean("number_pop_up",false);
                editor.apply();
                check_area_code_exists();

            }

      });

        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                editor.putBoolean("number_pop_up",false);
                editor.apply();
                dialog.dismiss();
            }

        });

        builder.show();

        editor.putString("number_num",num_number);
        editor.apply();
        editor.putString("number_id",num_id);
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

            a.setVisibility(View.VISIBLE);
            disclaimer.setVisibility(View.INVISIBLE);
            while (data.moveToNext()) {

                num_list.add(data.getString(1));
            }

            ListAdapter l_adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, num_list);
            a.setAdapter(l_adapter);

        }

        result = data;
    }

    public void query_result(Boolean result)
    {
        exist = result;

        if (exist)
        {
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtra("area_code", num_number);
            editor.apply();
            startActivity(intent);

        }

        else
        {
            builder = new android.support.v7.app.AlertDialog.Builder(context);
            builder.setMessage("Sorry this area code is not in our database, we hope to have this information for you at a later date!");
            builder.setCancelable(true);
            builder.setPositiveButton("OK", null);
            builder.show();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        pop_up = sharedPref.getBoolean("number_pop_up", false);

        if (pop_up)
        {
            num_number = sharedPref.getString("number_num",null);
            num_id = sharedPref.getString("number_id",null);
            alert();
        }

    }
}
