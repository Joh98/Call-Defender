package com.aa.calldefender;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

/**
 * Created by admin on 20/04/2018.
 */

public class BackgroundReadFromAreaCodeTable extends AsyncTask<String,Void, Cursor> {

    Context ctx;
    private DHelper database;
    public View_Area_Codes_Fragment viewer;


    BackgroundReadFromAreaCodeTable(Context ctx, View_Area_Codes_Fragment vacf)
    {
        this.ctx = ctx;
        viewer = vacf;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Cursor doInBackground(String... params) {
        database = new DHelper(ctx);
        int identifier = Integer.parseInt(params[0]);
        return database.getData(identifier);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Cursor result) {

        viewer.show_data(result);

    }

}
