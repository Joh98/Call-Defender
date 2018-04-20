package com.aa.calldefender;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by admin on 20/04/2018.
 */

public class BackgroundReadForMap extends AsyncTask<String,Void, List> {

    private DHelper database;
    public MapsActivity viewer;


    BackgroundReadForMap(MapsActivity ma)
    {
        viewer = ma;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List doInBackground(String... params) {
        database = new DHelper(viewer);
        return database.getAreaCodeForMap(params[0]);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List result) {
        viewer.show_Map(result);
    }

}
