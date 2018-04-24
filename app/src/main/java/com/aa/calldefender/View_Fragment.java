package com.aa.calldefender;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

public class View_Fragment extends Fragment {
    Cursor result;
    DHelper data;
    SharedPreferences.Editor editor = null;
    int frag_id; //set the fragment id to 1

    Context context = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.fragment_view, null);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("view_frag", Context.MODE_PRIVATE);
        frag_id = sharedPref.getInt("view_frag", 1);
        editor = sharedPref.edit();



        if (frag_id == 1) {
            View_Numbers_Fragment f = new View_Numbers_Fragment();


            editor.putInt("view_frag", 1);
            editor.apply();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, f).commit();


        }

        else if (frag_id == 2)
        {
            View_Area_Codes_Fragment f = new View_Area_Codes_Fragment();
            editor.putInt("view_frag", 2);
            editor.apply();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, f).commit();

        }
        return view;
    }


}

