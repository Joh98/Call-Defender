package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;

//Async task for the insertion of phone number/area code to the DB
public class BackgroundInsert extends AsyncTask<String,Void, Void> {

    //Declare variables
    Context ctx;

    //Set context
    BackgroundInsert(Context ctx)
    {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) { //Background task to be executed
        DHelper database = new DHelper(ctx); //Declare DB instance
        int identifier = Integer.parseInt(params[1]); //save query identifier to variable (parsed from a string)
        database.insertToDb(params[0], identifier); //run query where params[0] is the phone number/area code
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

