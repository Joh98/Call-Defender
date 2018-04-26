package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;

//Async task that queries the DB to determine whether the phone number/area code that's calling the user exists
public class BackgroundNumberExist extends AsyncTask<String,Void, Boolean> {

    //Declare variables
    Context ctx;
    private DHelper database;
    public CallInterceptor viewer;


    //Set contexts
    BackgroundNumberExist(Context ctx, CallInterceptor ci)
    {
        this.ctx = ctx;
        viewer = ci;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) { //background task to be executed

        database = new DHelper(ctx); //create new DB instance
        boolean a = database.num_exist(params[0]); //run query to determine if incoming number should be blocked
        boolean b = database.area_code_exist(params[0], 1);  //run query to determine is incoming number contains a blocked are code

        return a || b; //return

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) { //After task has executed

        //Call function within 'CallInterceptor' stating whether the incoming call should be blocked
        viewer.exist(result);
    }

}

