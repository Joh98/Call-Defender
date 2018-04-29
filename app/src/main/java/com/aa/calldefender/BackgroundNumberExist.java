package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;

//Async task that queries the DB to determine whether the phone number/area code that's calling the user exists
public class BackgroundNumberExist extends AsyncTask<String,Void, Boolean> {

    //Declare variables
    Context ctx;
    private CallInterceptor callInterceptor;

    //Set contexts
    BackgroundNumberExist(Context ctx, CallInterceptor ci)
    {
        this.ctx = ctx;
        callInterceptor = ci;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) { //Background task to be executed

        DHelper database = new DHelper(ctx); //Declare DB instance
        boolean a = database.numExist(params[0]); //Run query to determine if incoming number should be blocked
        boolean b = database.areaCodeExist(params[0], 1);  //Run query to determine is incoming number contains a blocked area code

        return a || b; //Return
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) { //After task has executed

        //Call function within 'CallInterceptor' stating whether the incoming call should be blocked
        callInterceptor.exist(result);
    }
}
