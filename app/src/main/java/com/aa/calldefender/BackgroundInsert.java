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

public class BackgroundInsert extends AsyncTask<String,Void, Void> {

    Context ctx;
    private DHelper database;

    BackgroundInsert(Context ctx)
    {
        this.ctx = ctx;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        database = new DHelper(ctx);
        int identifier = Integer.parseInt(params[1]);
        database.insert_to_db(params[0], identifier);
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}

