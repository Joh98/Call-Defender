package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;

//Async task that queries the DB to determine whether the phone number/area code entered by a user
//already exists in the DB before insertion
public class BackgroundNumberExistForAddingNumber extends AsyncTask<String,Void, Boolean> {

    //Declare variables
    Context ctx;
    private AddFragment addFragment;
    boolean result = false;

    //Set contexts
    BackgroundNumberExistForAddingNumber(Context ctx, AddFragment af)
    {
        this.ctx = ctx;
        addFragment = af;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) { //Background task to be executed

        DHelper database = new DHelper(ctx); //Declare DB instance
        int identifier = Integer.parseInt(params[1]); //Save query identifier to variable (parsed from a string)

        //Run query to check if the phone number or area code exists depending on the value of identifier
        if (identifier == 1) { //1 runs number query
            result = database.numExist(params[0]);
        }
        else if (identifier == 2) { //2 runs area code query
            result = database.areaCodeExist(params[0], 1);
        }

        return result; //Return the query result
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) { //After task has executed

        //Call function within 'AddFragment' that will determine if the number/area code will be added to the DB
        addFragment.numAlreadyExists(result);
    }
}
