package com.example.ghp.myapp_eventin;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by ghp on 31-Jul-16.
 */
public class LocationActivity extends AppCompatActivity implements LocationListener{

    private String latituteField;
    private String longitudeField;
    private LocationManager locationManager;
    private String provider;
    Context context;
    RadioGroup radioGroup;
    RadioButton radiobutton,radiobutton1;
    String radioButtonSelected="Current Location";
    EditText editText;
    String city="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
//        locationManager= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager!=null){
            locationManager.removeUpdates(this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = (location.getLatitude());
        double lng = (location.getLongitude());

        Log.d("xxxxxxxx",String.valueOf(lat)+"----------***"+String.valueOf(lng));

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                    Log.d("sdfd",returnedAddress.getLocality());
                }
//                myAddress.setText(strReturnedAddress.toString());
                Log.d("sdfd",returnedAddress.getLocality());;
                city=returnedAddress.getLocality();

            }
            else{
                Log.d("No addres","No address");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        latituteField.setText(String.valueOf(lat));
//        longitudeField.setText(String.valueOf(lng));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    public void currentLocation(View view){
        Toast.makeText(this, "Fetching current location",
                Toast.LENGTH_SHORT).show();
        Log.d("dfsdf","sfsf");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d("dfd",String.valueOf(enabled));

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Log.d("d","Location not avaialble");
        }

    }

public void onRadioSelected(View view){

    radioGroup = (RadioGroup) findViewById(R.id.radioGroup);


    Log.d("ssf","dsfsdf");
    int selectedId = radioGroup.getCheckedRadioButtonId();

    // find the radiobutton by returned id
    radiobutton = (RadioButton) findViewById(selectedId);
    Log.d("ggg",radiobutton.getText().toString());
    radioButtonSelected=radiobutton.getText().toString();


}
    public void onLocationClick(View view){
        Log.d("dfsdf",radioButtonSelected);

        if(radioButtonSelected.equalsIgnoreCase("Current City")){
            Log.d("dfd","dsfdsf");

            Toast.makeText(this, "Fetching current location",
                    Toast.LENGTH_SHORT).show();
            Log.d("dfsdf","sfsf");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);

            boolean enabled = service
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.d("dfd",String.valueOf(enabled));

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
            if (!enabled) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
            if (location != null) {
                System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            } else {
                Log.d("d","Location not avaialble");
            }


            Intent intent = new Intent(LocationActivity.this, CategoryActivity.class);
            intent.putExtra("city",city);
//                        intent.putExtra("position",position);
            startActivity(intent);





        }else{
            editText= (EditText) findViewById(R.id.locationPreference);

            Log.d("dsfdsf",editText.getText().toString());
            Intent intent = new Intent(LocationActivity.this, CategoryActivity.class);
            intent.putExtra("city",editText.getText().toString());
//                        intent.putExtra("position",position);
            startActivity(intent);

        }
    }

    public void onCancel(View view){
        Intent intent = new Intent(LocationActivity.this, CategoryActivity.class);

//                        intent.putExtra("position",position);
        startActivity(intent);

    }
}
