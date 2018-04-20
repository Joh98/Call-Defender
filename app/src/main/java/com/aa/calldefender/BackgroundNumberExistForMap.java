package com.aa.calldefender;

/**
 * Created by admin on 20/04/2018.
 */

import android.content.Context;
import android.os.AsyncTask;


/**
 * Created by admin on 20/04/2018.
 */

public class BackgroundNumberExistForMap extends AsyncTask<String,Void, Boolean> {

    Context ctx;
    private DHelper database;
    public View_Numbers_Fragment viewer;


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
    protected Boolean doInBackground(String... params) {
        database = new DHelper(ctx);
        return database.area_code_exist(params[0], 2);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {

        viewer.query_result(result);
    }

}

