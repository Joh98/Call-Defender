package com.aa.calldefender;

/**
 * Created by admin on 20/04/2018.
 */

import android.content.Context;
import android.os.AsyncTask;


/**
 * Created by admin on 20/04/2018.
 */

public class BackgroundNumberExistForAddingNumber extends AsyncTask<String,Void, Boolean> {

    Context ctx;
    private DHelper database;
    public Add_Fragment viewer;


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
    protected Boolean doInBackground(String... params) {

        database = new DHelper(ctx);
        boolean result = false;
        int identifer = Integer.parseInt(params[1]);

        if (identifer == 1)
        {
            result = database.num_exist(params[0]);
        }

        else if (identifer == 2)
        {
            result = database.area_code_exist(params[0], 1);
        }

        return result;

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {

        viewer.num_already_exists(result);
    }

}

