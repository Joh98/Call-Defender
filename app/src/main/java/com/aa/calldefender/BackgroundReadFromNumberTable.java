package com.aa.calldefender;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

//Async task for retrieving phone numbers from the DB
public class BackgroundReadFromNumberTable extends AsyncTask<String,Void, Cursor> {

    //Declare variables
    Context ctx;
    private DHelper database;
    public View_Numbers_Fragment viewer;

    //Set contexts
    BackgroundReadFromNumberTable(Context ctx, View_Numbers_Fragment vnf)
    {
        this.ctx = ctx;
        viewer = vnf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Cursor doInBackground(String... params) { //background task to be executed
        database = new DHelper(ctx); //Create new DB instance
        int identifier = Integer.parseInt(params[0]); //save query identifier to variable (parsed from a string)
        return database.getData(identifier); //run and return result of the query
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Cursor result) { //After task has executed

        //Call function within 'View_Numbers_Fragment' so query data can be passed back and used
        viewer.show_data(result);

    }

}
