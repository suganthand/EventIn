package com.example.ghp.myapp_eventin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ghp on 26-Jul-16.
 */
public class EventActivity extends AppCompatActivity implements OnMapReadyCallback {

    ArrayList<Event> eventList;
    int eventPos;
    TextView title;
    TextView startDate;
    TextView endDate;
    TextView location;
    TextView description;
    private GoogleMap mMap;
    String gLocation="";
    String eventTitle="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("pos------","dfdf");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        eventPos = intent.getIntExtra("position",0);
        Log.d("pos------",String.valueOf(eventPos));
        Bundle bundle= getIntent().getExtras();
        if(bundle!=null){
//            Log.d("qqqqqqqqqqqqqqq","qqqqqqqqqqqqqqqq");
    eventList = (ArrayList<Event>)getIntent().getExtras().getSerializable("event");
}else{
    eventList= Test.eventList;
}
//        ArrayList<Event> eventsFound = intent.getE;


//        eventList = (ArrayList<Event>)getIntent().getExtras().getSerializable("event");


        Log.d("asa",String.valueOf(eventList.get(eventPos).title));
        Log.d("asa",String.valueOf(eventList.get(eventPos).date_start));
        Log.d("asa",String.valueOf(eventList.get(eventPos).location));
        Log.d("asa",String.valueOf(eventList.get(eventPos).description));
        setValuesforActivity();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gLocation = getGeocoderString(location.getText().toString());
        if(eventList!=null){
            Test.eventList=eventList;
        }

        Log.d("as",gLocation);
//        setUpMapIfNeeded();
//        mMap.clear();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Log.d("ssssssss",item.toString());
        if(item.toString().contains("Location")){

            Intent intent = new Intent(EventActivity.this, LocationActivity.class);
            startActivity(intent);

        }else if(item.toString().contains("Logout")){
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(EventActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void setValuesforActivity(){
        Log.d("asasasa",String.valueOf(eventList.get(eventPos).url));
        title= (TextView)  findViewById(R.id.txtTitle);
        startDate = (TextView) findViewById(R.id.txtStartDate);
        location = (TextView) findViewById(R.id.txtLocation);
        description = (TextView) findViewById(R.id.description);
        endDate= (TextView) findViewById(R.id.txtEndDate);

        title.setText(eventList.get(eventPos).title);
        startDate.setText(eventList.get(eventPos).date_start);
        location.setText(eventList.get(eventPos).location);
        endDate.setText(eventList.get(eventPos).date_stop);
        if(eventList.get(eventPos).description.equalsIgnoreCase("null")){
            description.setText("No description avaialble");
        }else{
            description.setText(Html.fromHtml(eventList.get(eventPos).description));
        }

        description.setMovementMethod(new ScrollingMovementMethod());
        eventTitle= (String) title.getText();
    }


    private String getGeocoderString(String location){
        StringBuilder builder = new StringBuilder();
        String[] parts = location.split(" ");
        for(int i=0;i<parts.length;i++){
            builder.append(parts[i].trim() + "+");
        }
        return builder.toString();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("sdad","im here");
        mMap = googleMap;
        new GecodeMapsLocationTask().execute();
    }


    private class GecodeMapsLocationTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {

                Log.d("gLocation=",gLocation);
                String requestString = "http://maps.google.com/maps/api/geocode/json?address=" + gLocation +"&sensor=false";
                response = getLatLongByURL(requestString);
                Log.d("response",""+response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result)  {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                Log.d("latitude", "" + lat);
                Log.d("longitude", "" + lng);
                double actualLat= Double.parseDouble(eventList.get(eventPos).latitude);
                double actualLong= Double.parseDouble(eventList.get(eventPos).longitude);
                Log.d("latitude", eventList.get(eventPos).latitude);
                Log.d("longitude", "" + eventList.get(eventPos).longitude);
                // Add marker and move camera
                LatLng latLng = new LatLng(actualLat, actualLong);
                mMap.addMarker(new MarkerOptions().position(latLng).title(title.getText().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                //Zoom to the location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                        12.0f));

            } catch (JSONException e) {
                Log.d("", "Error receiving data from google geocoder API");
                e.printStackTrace();
            }
        }
    }


    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
                Log.d("", "Error receiving data from google geocoder API");
            }

        } catch (Exception e) {
            Log.d("", "Error receiving data from google geocoder API");
            e.printStackTrace();
        }
        return response;
    }
    public void onClickNotify(View view)
    {

        Log.d("clicked","clicked");
//        new SendMessage().execute();
        Intent intent = new Intent(EventActivity.this, ContactActivity.class);

                        intent.putExtra("eventTitle",eventTitle);
        startActivity(intent);
    }

    public void onClickNotify1(View view)
    {

        Log.d("clicked","clicked");
//        new SendMessage().execute();
        Intent intent = new Intent(EventActivity.this, NotifyActivity.class);

        intent.putExtra("eventTitle",eventTitle);
        startActivity(intent);
    }

    private class SendMessage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d("sfas","onPostExecute");

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(
                    "https://api.twilio.com/2010-04-01/Accounts/ACc1cfbdf5c1b6be55e8243e9b54c427ba/SMS/Messages");
            String base64EncodedCredentials = "Basic "
                    + Base64.encodeToString(
                    ("ACc1cfbdf5c1b6be55e8243e9b54c427ba" + ":" +"96f156837a9225adf235ac1b15301364").getBytes(),
                    Base64.NO_WRAP);

            httppost.setHeader("Authorization",
                    base64EncodedCredentials);
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("From",
                        "+14125047154"));
                nameValuePairs.add(new BasicNameValuePair("To",
                        "+14127994329"));
                nameValuePairs.add(new BasicNameValuePair("Body",
                        "Hi! I am going to "+eventTitle));


                httppost.setEntity(new UrlEncodedFormEntity(
                        nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                System.out.println("Entity post is: "
                        + EntityUtils.toString(entity));


            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
    }

