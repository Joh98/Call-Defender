package com.aa.calldefender;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

//Async task for retrieving area codes from the DB
public class BackgroundReadFromAreaCodeTable extends AsyncTask<String,Void, Cursor> {

    //Declare variables
    Context ctx;
    private ViewAreaCodesFragment viewAreaCodesFragment;

    //Set contexts
    BackgroundReadFromAreaCodeTable(Context ctx, ViewAreaCodesFragment vacf)
    {
        this.ctx = ctx;
        viewAreaCodesFragment = vacf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Cursor doInBackground(String... params) { //Background task to be executed
        DHelper database = new DHelper(ctx); //Declare DB instance
        int identifier = Integer.parseInt(params[0]); //Save query identifier to variable (parsed from a string)
        return database.getData(identifier); //Run and return result of the query
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Cursor result) { //After task has executed

        //Call function within 'ViewAreaCodesFragment' so query data can be passed back and used
        viewAreaCodesFragment.showData(result);
    }
}
