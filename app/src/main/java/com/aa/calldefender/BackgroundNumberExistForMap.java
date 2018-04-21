package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;

//Async task that quires the DB to determine if the number the user has selected has any map info
public class BackgroundNumberExistForMap extends AsyncTask<String,Void, Boolean> {

    //Declare variables
    Context ctx;
    private DHelper database;
    public View_Numbers_Fragment viewer;

    //Set contexts
    BackgroundNumberExistForMap(Context ctx, View_Numbers_Fragment vnf)
    {
        this.ctx = ctx;
        viewer = vnf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) { //background task to be executed
        database = new DHelper(ctx); //Create new DB instance
        return database.area_code_exist(params[0], 2); //run and return result of the query
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) { //After task has executed
        //Call function within 'View_Numbers_Fragment' that will determine if the map activity should start
        viewer.query_result(result);
    }

}

