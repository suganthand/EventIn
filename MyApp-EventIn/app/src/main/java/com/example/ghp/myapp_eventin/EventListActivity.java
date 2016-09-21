package com.example.ghp.myapp_eventin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.facebook.login.LoginManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class EventListActivity extends AppCompatActivity {

    ArrayList<Event> eventsFound;
    public String query,query1 = "";
    private Context context;

    String category="";
    String city="";
    public EventListActivity() {
        eventsFound = new ArrayList<Event>();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        String apiKey = "P4hD778M2qgmhk4v";
//        Log.d("categoryselcted",category);
        Intent intent = getIntent();
if(intent!=null){
//    category= intent.getExtras().getString("category");
    category= intent.getStringExtra("category");
    city=intent.getStringExtra("city");

    Log.d("cile path",Test.filePath);
    if(category!=null){
        Test.filePath= category;
    }
    if(city!=null){
        Test.city=city.replaceAll(" ","");
        Log.d("QQQQQQQQQQQQQ",city);
    }

    Log.d("ciledd path",Test.filePath);
    TextView categoryName= (TextView) findViewById(R.id.categoryName);
    categoryName.setText(Html.fromHtml(Test.filePath));

}
        String url = "http://api.eventful.com/json/events/search?app_key=" + apiKey;
        query= url+"&category="+Test.filePath+"&location="+Test.city+"&date=Future&sort_order=date&page_number=1&page_size=20";
        query=query.replaceAll(" ","");
//        http://api.eventful.com/json/events/search?app_key=2Dmmk5bhzHWSLJzj&category=music&location=pittsburgh&date=Future&page_number=1&page_size=20
        new HttpAsyncTask(EventListActivity.this).execute(query);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Log.d("ssssssss",item.toString());
        if(item.toString().contains("Location")){

            Intent intent = new Intent(EventListActivity.this, LocationActivity.class);
            startActivity(intent);

        }else if(item.toString().contains("Logout")){
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(EventListActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog;

        public HttpAsyncTask(EventListActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Events. Please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {

            Log.d("URL--------", urls[0]);
            try {
                return GET(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Log.d("sfsdf",result);

            try{
                JSONObject jsonObject =  new JSONObject(result);
                JSONObject events = jsonObject.getJSONObject("events");
                JSONArray event = events.getJSONArray("event");
//                Log.d("WEB", "Nombre d'événements: " + event.length());

                for (int j = 0; j < event.length(); j++) {
                    JSONObject item = event.getJSONObject(j);
                    //Si la valeur sous "stop_time" est null
                    if (item.isNull("stop_time")) {
                        eventsFound.add(new Event(item.getString("id"),
                                item.getString("title"),
                                item.getString("start_time"),
                                "2020-01-01",//TODO Important
                                item.getString("city_name"),
                                item.getString("description"),
                                item.getString("latitude"),
                                item.getString("longitude"),
                                item.getString("url")));
                    } else {
                        eventsFound.add(new Event(item.getString("id"),
                                item.getString("title"),
                                item.getString("start_time"),
                                item.getString("stop_time"),
                                item.getString("city_name"),
                                item.getString("description"),
                                item.getString("latitude"),
                                item.getString("longitude"),
                                item.getString("url")));
                    }
                }

                Log.d("wewe",String.valueOf(eventsFound.size()));
//                for(Event i: eventsFound){
//                    Log.d("eventseventful", "event "+ i.title + " " +i.date_start+ " " +i.date_stop+ " "
//                            +i.idFromEventful+ " " +i.location+ " " +i.description);
//
//                }
            }catch(Exception e){
                Log.d("Error: ", e.getMessage());
            }

            populateInList();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }


        }
        public void populateInList(){
            final ListView listView = (ListView) findViewById(R.id.activity_list);

            if(EventListActivity.this !=null){
                final EventsListAdapter adapter = new EventsListAdapter(EventListActivity.this, eventsFound);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        Log.d("abc",String.valueOf(position));
                        Log.d("dsf",String.valueOf(adapter.get(position).title));
                        Intent intent = new Intent(EventListActivity.this, EventActivity.class);
                        intent.putExtra("event",eventsFound);
                        intent.putExtra("position",position);
                        startActivity(intent);

                    }
                });
            }



        }
    }

    public static String GET(String url) throws IOException {
//        InputStream inputStream = null;
//        String result = "";
//        try {
//
//            // create HttpClient
//            HttpClient httpclient = new DefaultHttpClient();
//
//            // make GET request to the given URL
//            Log.d("url++++++",url);
//            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
//
//            // receive response as inputStream
//            inputStream = httpResponse.getEntity().getContent();
//
//            // convert inputstream to string
//            if(inputStream != null)
//                result = convertInputStreamToString(inputStream);
//            else
//                result = "Did not work!";
//
//        } catch (Exception e) {
//            Log.d("InputStream", e.getLocalizedMessage());
//        }
//
//        return result;


        URL url1 = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) url1.openConnection();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            bufferedReader.close();
            Log.d("stringB",stringBuilder.toString());
            return stringBuilder.toString();
        }
        finally{
            urlConnection.disconnect();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();

        return result;

    }

    private void getEvents(String query) {
        Log.d("dddddd", "im here");
        int page_count = 0;
//        try{
//        new getHttp().execute();
//            HttpEntity page =new getHttp().execute();

//            JSONObject js = new JSONObject(EntityUtils.toString(page, HTTP.UTF_8));
//            page_count = Integer.parseInt(js.getString("page_count"));
//        } catch (ClientProtocolException e) {
//            Log.d("HTTP ","Error: "+e.getMessage());
//        } catch (IOException e) {
//            Log.d("Web ","Error: "+e.getMessage());
//        } catch (ParseException e) {
//            Log.d("Parse ","Error: "+e.getMessage());
//        } catch (JSONException e) {
//            Log.d("JSON ","Error: "+e.getMessage());
//        }
        //int i = 0;
//        int i =  page_count - 1; //TODO ! Juste pour abréger ici on demande une seule page
//        while(i < page_count) {
//            try {
//                HttpEntity page = getHttp(query+"&page_number="+Integer.toString(i+1));
//                JSONObject js = new JSONObject(EntityUtils.toString(page, HTTP.UTF_8));
//                JSONObject events = js.getJSONObject("events");
//                JSONArray event = events.getJSONArray("event");
//                Log.d("WEB", "Nombre d'événements: " + event.length());
//
//                for (int j = 0; j < event.length(); j++) {
//                    JSONObject item = event.getJSONObject(j);
//                    //Si la valeur sous "stop_time" est null
//                    if (item.isNull("stop_time")) {
//                        eventsFound.add(new Event(item.getString("id"),
//                                item.getString("title"),
//                                item.getString("start_time"),
//                                "2020-01-01",//TODO Important
//                                item.getString("city_name"),
//                                item.getString("description")));
//                    } else {
//                        eventsFound.add(new Event(item.getString("id"),
//                                item.getString("title"),
//                                item.getString("start_time"),
//                                item.getString("stop_time"),
//                                item.getString("city_name"),
//                                item.getString("description")));
//                    }
//                }
//
//            } catch (ClientProtocolException e) {
//                Log.d("HTTP ", "Erreur: " + e.getMessage());
//            } catch (IOException e) {
//                Log.d("Web ", "Erreur: " + e.getMessage());
//            } catch (ParseException e) {
//                Log.d("Parse ", "Erreur: " + e.getMessage());
//            } catch (JSONException e) {
//                Log.d("JSON ", "Erreur: " + e.getMessage());
//            }
//            i++;
//        }
    }

//    private class getHttp extends AsyncTask<String, Void, String> {
//
//        String myString = "";
//
//        protected String doInBackground(String... params) {
//            String response2 = "";
//            StringBuffer chaine = new StringBuffer("");
//            try {
//                Log.d("11111", query);
//
////                URL url = new URL(query);
////                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
////                Log.d("sfs","sfsf");
//////                conn.setReadTimeout(15000);
//////                conn.setConnectTimeout(15000);
////                conn.setRequestMethod("GET");
////                conn.setDoInput(true);
////                conn.setRequestProperty("Content-Type",
////                        "application/x-www-form-urlencoded");
////                conn.setDoOutput(true);
////                Log.d("11111", "1222222222222222222222"+conn.getContent().toString());
////                int responseCode = conn.getResponseCode();
////                Log.d("11111", "111333333333333333"+String.valueOf(responseCode));
////                if (responseCode == HttpsURLConnection.HTTP_OK) {
////                    Log.d("sdfdsf","rere");
////                    String line;
////                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
////
////                    while ((line = br.readLine()) != null) {
////                        response2 += line;
////                    }
////
////                    Log.d("bbb", response2);
////
////                }
//
//
//                HttpClient httpClient = new DefaultHttpClient();
//                HttpGet http = new HttpGet(query);
//                HttpResponse response = httpClient.execute(http);
//                Log.d("sfsf",response.getEntity().toString());
//
//                return myString;
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return myString;
//
//        }
//    }


    private class MyAsyncTask extends AsyncTask<String, String, ArrayList<String>> {


        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> event_details = new ArrayList<String>();
            EventfulAPI web = new EventfulAPI();
            String id = params[0];
            event_details = web.getEventDetails(id);
            return event_details;
        }

    }
}