package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

//Async task for the deletion of a phone number from the DB
public class BackgroundDeleteFromNumberTable extends AsyncTask<String,Void, String> {

    //Declare variables
    Context ctx;
    private ViewNumbersFragment viewNumbersFragment;

    //Set contexts
    BackgroundDeleteFromNumberTable(Context ctx, ViewNumbersFragment vnf) {

        this.ctx = ctx;
        viewNumbersFragment = vnf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) { //Background task to be executed

        DHelper database = new DHelper(ctx); //Declare DB instance
        String phone_number = params[0]; //Save phone number to variable
        int identifier = Integer.parseInt(params[1]); //Save query identifier to variable (parsed from a string)
        return database.deleteFromDb(phone_number, identifier); //Run the query and return
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) { //After task has executed

        Log.d("BackgroundDeleteFromDB", "onPostExecute: " + result); //Debug

        //Call function within 'ViewNumbersFragment' to re-display the phone numbers to the user
        viewNumbersFragment.reDisplay();

    }

}

