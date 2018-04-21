package com.aa.calldefender;
import android.content.Context;
import android.os.AsyncTask;

//Async task that queries the DB to determine whether the phone number/area code entered by a user
//already exists in the DB before insertion
public class BackgroundNumberExistForAddingNumber extends AsyncTask<String,Void, Boolean> {

    //Declare variables
    Context ctx;
    private DHelper database;
    public Add_Fragment viewer;
    boolean result = false;

    //Set contexts
    BackgroundNumberExistForAddingNumber(Context ctx, Add_Fragment af)
    {
        this.ctx = ctx;
        viewer = af;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) { //background task to be executed

        database = new DHelper(ctx); //Create new DB instance

        int identifier = Integer.parseInt(params[1]); //Save query identifier to variable (parsed from a string)

        //Run query to check if the phone number or area code exists depending on the value of identifier
        if (identifier == 1)
        {
            result = database.num_exist(params[0]);
        }

        else if (identifier == 2)
        {
            result = database.area_code_exist(params[0], 1);
        }

        return result; //Return the query result

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) { //After task has executed

        //Call function within 'Add_Fragment' that will determine if the number/area code should be added to the DB
        viewer.num_already_exists(result);
    }

}

