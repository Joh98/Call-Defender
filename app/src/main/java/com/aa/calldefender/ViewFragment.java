package com.aa.calldefender;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//Class for the View Fragment that determines what fragment should be loaded in (ViewNumbersFragment or ViewAreaCodesFragment)
public class ViewFragment extends Fragment {

    //Declare variables
    Cursor result;
    DHelper data;
    SharedPreferences.Editor editor = null;
    int frag_id;
    Context context = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) { //On creation


        View view =  inflater.inflate(R.layout.fragment_view, null); //Set the view

        //Access and save shared preferences to variables
        SharedPreferences sharedPref = getActivity().getSharedPreferences("view_frag", Context.MODE_PRIVATE);
        frag_id = sharedPref.getInt("view_frag", 1);

        editor = sharedPref.edit(); //Set up shared preferences editor

        if (frag_id == 1) { //If the last fragment loaded in was the ViewNumbersFragment

            //Set the 'last loaded in fragment' to the ViewNumbersFragment
            editor.putInt("view_frag", 1);
            editor.apply();

            //Load the fragment in
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, new ViewNumbersFragment()).commit();
        }
        else if (frag_id == 2) { //Else if the last fragment loaded in was the ViewAreaCodesFragment

            //Set the 'last loaded in fragment' to the ViewAreaCodesFragment
            editor.putInt("view_frag", 2);
            editor.apply();

            //Load the fragment in
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, new ViewAreaCodesFragment()).commit();
        }

        return view; //Return view
    }
}

