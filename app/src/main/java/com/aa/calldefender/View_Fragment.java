package com.aa.calldefender;


import android.app.AlertDialog;
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

public class View_Fragment extends Fragment {
    ArrayList<String> num_list = new ArrayList<>();
    ArrayList<String> num_list_2 = new ArrayList<>();
    Cursor result;
    Cursor result_2;
    ListView a;
    ListView b;
    DHelper data;
    android.support.v7.app.AlertDialog.Builder builder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_view, null);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new View_Numbers_Fragment())
                .commit();

        return view;
    }



}

