package com.example.ghp.myapp_eventin;

/**
 * Created by ghp on 30-Jul-16.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ContactActivity extends AppCompatActivity {

    // ArrayList
    ArrayList<SelectUser> selectUsers;
    ArrayList<String> selectedNumbers;
    ArrayList<String> selectedUsers;
    List<SelectUser> temp;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones, email;

    // Pop up
    ContentResolver resolver;
    SearchView search;
    SelectUserAdapter adapter;
    String eventTitle="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Intent intent = getIntent();
        eventTitle = intent.getStringExtra("eventTitle");
        Log.d("eventTitle---------",eventTitle);
        selectUsers = new ArrayList<SelectUser>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contacts_list);
        Log.d("dff",listView.toString());

        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();

        search = (SearchView) findViewById(R.id.searchView);

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                adapter.filter(newText);
                return false;
            }
        });
    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(ContactActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "--------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SelectUser selectUser = new SelectUser();
                    selectUser.setThumb(bit_thumb);
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    selectUser.setEmail(id);
                    selectUser.setCheckedBox(false);
                    selectUsers.add(selectUser);
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SelectUserAdapter(selectUsers, ContactActivity.this);
            listView.setAdapter(adapter);
                Log.d("aaa",adapter._data.toString());
//            Log.d("aaa",adapter.);
            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Log.e("search", "here---------------- listener");

                    SelectUser data = selectUsers.get(i);
                }
            });

            listView.setFastScrollEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }
    public void doneClicked(View view){
        selectedNumbers=new ArrayList<>();
        selectedUsers=new ArrayList<>();
        Log.d("ddddd",String.valueOf(adapter.selectedNumbers.size()));
        Log.d("eeee",String.valueOf(adapter.selectedUsers.size()));
//        Log.d("ddddd",String.valueOf(adapter._data.));
        Log.d("fffff",String.valueOf(adapter._data.size()));
        selectedNumbers.addAll(adapter.selectedNumbers);
        if(adapter.selectedNumbers.size()>0){


            new SendMessage().execute();
            Toast.makeText(this, "Message sent!",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ContactActivity.this, NotifyActivity.class);

            startActivity(intent);

        }

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

                for (String number: selectedNumbers){
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("From",
                            "+14125047154"));


                    nameValuePairs.add(new BasicNameValuePair("To",number));

//                nameValuePairs.add(new BasicNameValuePair("To",
//                        "+14127994329"));
                    if(eventTitle.length()>=160){
                        eventTitle=eventTitle.substring(0,159);
                    }
                    nameValuePairs.add(new BasicNameValuePair("Body",
                            "Hi! I am going to "+eventTitle));


                    httppost.setEntity(new UrlEncodedFormEntity(
                            nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    System.out.println("Entity post is: "
                            + EntityUtils.toString(entity));


                }

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
