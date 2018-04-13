package com.aa.calldefender;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by admin on 13/04/2018.
 */

public class BackgroundDBTasks extends AsyncTask<String,Void, Cursor> {

    Context ctx;
    private DHelper database;
    private View_Numbers_Fragment viewer;


    BackgroundDBTasks(Context ctx)
    {
        this.ctx = ctx;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Cursor doInBackground(String... params) {
        database = new DHelper(ctx);
        String method = params[0];
        if (method.equals("insert"))
        {
            String data = params[1];
            int identifier = Integer.parseInt(params[2]);
            database.insert_to_db(data, identifier);
        }

        else if (method.equals("view"))
        {
            int identifier = Integer.parseInt(params[1]);
            return database.getData(identifier);

        }

        else if (method.equals("delete"))
        {
            String data = params[1];
            int identifier = Integer.parseInt(params[2]);
            database.delete_from_db(data, identifier);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Cursor result) {


        //viewer = new View_Numbers_Fragment();

    }

}
