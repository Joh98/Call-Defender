package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

//Async task for the deletion of area code
public class BackgroundDeleteFromAreaCodeTable extends AsyncTask<String,Void, String> {

    //Declare variables
    Context ctx;
    private DHelper database;
    public View_Area_Codes_Fragment viewer;

    //Set contexts
    BackgroundDeleteFromAreaCodeTable(Context ctx, View_Area_Codes_Fragment vacf)
    {
        this.ctx = ctx;
        viewer = vacf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) { //background task to be executed
        database = new DHelper(ctx); //create new DB instance
        String data = params[0]; //save area code to variable
        int identifier = Integer.parseInt(params[1]); //save query identifier to variable (parsed from a string)
        return database.delete_from_db(data, identifier); //run the query and return

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) { //After task has executed

        Log.d("BackgroundDeleteFromDB", "onPostExecute: " + result); //Debug

        //Call function within 'View_Area_Codes_Fragment' to re-display the area codes to the user
        viewer.re_display();

    }

}

