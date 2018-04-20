package com.aa.calldefender;

/**
 * Created by admin on 20/04/2018.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


/**
 * Created by admin on 20/04/2018.
 */

public class BackgroundNumberExist extends AsyncTask<String,Void, Boolean> {

    Context ctx;
    private DHelper database;
    public CallInterceptor viewer;


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
    protected Boolean doInBackground(String... params) {

        database = new DHelper(ctx);
        boolean a = database.num_exist(params[0]);
        boolean b = database.area_code_exist(params[0], 1);

        return a || b;

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        viewer.exist(result);
    }

}

