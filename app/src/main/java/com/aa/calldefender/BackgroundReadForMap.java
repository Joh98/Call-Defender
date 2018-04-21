package com.aa.calldefender;
import android.os.AsyncTask;
import java.util.List;

//Async task that quires the DB for the required data for the MapsActivity
public class BackgroundReadForMap extends AsyncTask<String,Void, List> {

    //Declare variables
    private DHelper database;
    public MapsActivity viewer;

    //Set context
    BackgroundReadForMap(MapsActivity ma)
    {
        viewer = ma;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List doInBackground(String... params) { //background task to be executed
        database = new DHelper(viewer); //Create new DB instance
        return database.getAreaCodeForMap(params[0]); //run and return result of DB query
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List result) { //After execution

        //Call function within MapsActivity so that the query data can be passed back and used for the map
        viewer.show_Map(result);
    }

}
