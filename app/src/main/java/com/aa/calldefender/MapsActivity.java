package com.aa.calldefender;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

//Class for activity that displays Google Map data
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Declare variables
    private GoogleMap mMap;
    String num;
    List result;
    TextView number_display;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //On creation

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); //Set the view

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        num = getIntent().getExtras().getString("phone_number_for_map"); //Save phone number to a variable from data that is stored in the intent

        //Set variable to represent the TextView and set its text to that of the phone number stored in the intent
        number_display = (TextView)findViewById(R.id.phone_number);
        number_display.setText(num);

        mapFragment.getMapAsync(this); //Initialises the map
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //Function that gets called automatically after map has been initialised
        mMap = googleMap;
        new BackgroundReadForMap(this).execute(num); //Run async DB task to grab data that will be used on the map's display

    }

    public void showMap(List data) //Function used by the 'onPostExecute' of 'BackGroundReadForMap'
    {
        result = data; //Save the query data to a list variable

        //Extract data from the list and save to variables
        String area_code = (String) result.get(0); //Area code of phone number
        Float lo = Float.parseFloat((String) result.get(1)); //Longitude
        Float la = Float.parseFloat((String) result.get(2)); //Latitude
        String title = (String) result.get(3); //Phone number

        //Add pin to the map using the list data and zoom the camera in
        LatLng location = new LatLng(la, lo);
        mMap.addMarker(new MarkerOptions().position(location).title("Approx location is " + title + " (" + area_code +")")).showInfoWindow();;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 8.0f));
    }
}
