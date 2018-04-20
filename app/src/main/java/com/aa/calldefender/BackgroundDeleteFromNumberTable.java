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

public class BackgroundDeleteFromNumberTable extends AsyncTask<String,Void, String> {

    Context ctx;
    private DHelper database;
    public View_Numbers_Fragment viewer;


    BackgroundDeleteFromNumberTable(Context ctx, View_Numbers_Fragment vnf)
    {
        this.ctx = ctx;
        viewer = vnf;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        database = new DHelper(ctx);
        String data = params[0];
        int identifier = Integer.parseInt(params[1]);
        return database.delete_from_db(data, identifier);

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {

        Log.d("BackgroundDeleteFromDB", "onPostExecute: " + result);

        viewer.re_display();

    }

}

