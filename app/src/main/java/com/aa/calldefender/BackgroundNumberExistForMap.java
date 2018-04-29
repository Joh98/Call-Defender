package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;

//Async task that queries the DB to determine if the number the user has selected has any map info
public class BackgroundNumberExistForMap extends AsyncTask<String,Void, Boolean> {

    //Declare variables
    Context ctx;
    private ViewNumbersFragment viewNumbersFragment;

    //Set contexts
    BackgroundNumberExistForMap(Context ctx, ViewNumbersFragment vnf)
    {
        this.ctx = ctx;
        viewNumbersFragment = vnf;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) { //Background task to be executed
        DHelper database = new DHelper(ctx); //Declare DB instance
        return database.areaCodeExist(params[0], 2); //Run and return result of the query
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) { //After task has executed

        //Call function within 'ViewNumbersFragment' that will determine if the map activity should start
        viewNumbersFragment.queryResult(result);
    }
}
