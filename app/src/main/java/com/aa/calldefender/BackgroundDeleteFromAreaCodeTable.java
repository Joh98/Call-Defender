package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

//Async task for the deletion of area code from the DB
public class BackgroundDeleteFromAreaCodeTable extends AsyncTask<String,Void, String> {

    //Declare variables
    Context ctx;
    private ViewAreaCodesFragment viewAreaCodesFragment;

    //Set contexts
    BackgroundDeleteFromAreaCodeTable(Context ctx, ViewAreaCodesFragment vacf) {

        this.ctx = ctx;
        viewAreaCodesFragment = vacf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) { //Background task to be executed

        DHelper database = new DHelper(ctx); //Declare DB instance
        String area_code = params[0]; //Save area code to variable
        int identifier = Integer.parseInt(params[1]); //Save query identifier to variable (parsed from a string)
        return database.deleteFromDb(area_code, identifier); //Run the query and return
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) { //After task has executed

        Log.d("BackgroundDeleteFromDB", "onPostExecute: " + result); //Debug

        //Call function within 'ViewAreaCodesFragment' to re-display the area codes to the user
        viewAreaCodesFragment.reDisplay();
    }
}

