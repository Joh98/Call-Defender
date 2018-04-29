package com.aa.calldefender;
import android.os.AsyncTask;
import java.util.List;

//Async task that queries the DB for the required data for MapsActivity
public class BackgroundReadForMap extends AsyncTask<String,Void, List> {

    private MapsActivity mapsActivity;

    //Set context
    BackgroundReadForMap(MapsActivity ma) {
        mapsActivity = ma;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List doInBackground(String... params) { //Background task to be executed
        DHelper database = new DHelper(mapsActivity); //Declare DB instance
        return database.getAreaCodeForMap(params[0]); //Run and return result of DB query
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List result) { //After task has executed

        //Call function within 'MapsActivity' so that the query data can be passed back and used for the map
        mapsActivity.showMap(result);
    }
}
