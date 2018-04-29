package com.aa.calldefender;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

//Async task for retrieving phone numbers from the DB
public class BackgroundReadFromNumberTable extends AsyncTask<String,Void, Cursor> {

    //Declare variables
    Context ctx;
    private ViewNumbersFragment viewNumbersFragment;

    //Set contexts
    BackgroundReadFromNumberTable(Context ctx, ViewNumbersFragment vnf)
    {
        this.ctx = ctx;
        viewNumbersFragment = vnf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Cursor doInBackground(String... params) { //Background task to be executed
        DHelper database = new DHelper(ctx); //Declare DB instance
        int identifier = Integer.parseInt(params[0]); //Save query identifier to variable (parsed from a string)
        Cursor data = database.getData(identifier); //Run and return result of the query
        return data;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Cursor result) { //After task has executed

        //Call function within 'ViewNumbersFragment' so query data can be passed back and used
        viewNumbersFragment.showData(result);

    }

}
