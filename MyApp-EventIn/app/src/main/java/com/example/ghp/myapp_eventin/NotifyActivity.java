package com.example.ghp.myapp_eventin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;

/**
 * Created by ghp on 01-Aug-16.
 */
public class NotifyActivity extends AppCompatActivity {

    String eventTitle="";

    ArrayList<String> notify= new ArrayList<String>();
    final Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_notify);
        notify.add("Share on Facebook");
        notify.add("Share via SMS");

        Intent intent = getIntent();
        eventTitle = intent.getStringExtra("eventTitle");
        final ListView listView = (ListView) findViewById(R.id.list);
//
//        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
//                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
//                "Linux", "OS/2" };

        ArrayAdapter<String> files = new ArrayAdapter<String>(NotifyActivity.this,
                android.R.layout.simple_list_item_1,
                notify);

        listView.setAdapter(files);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);
                Log.d("dfdsf",itemValue);

                if(itemValue.contains("Facebook")){
                    facebookPost();
                }else if(itemValue.contains("SMS")){
                    sendsms();
                }


            }

        });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(this!=null && !isFinishing()){

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set title
                    alertDialogBuilder.setTitle("Beacon Detected!");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Click Yes to notify friends")
                            .setCancelable(false)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
//                        NotifyActivity.this.finish();
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            }
        }, 4000);


            }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Log.d("ssssssss",item.toString());
        if(item.toString().contains("Location")){

            Intent intent = new Intent(NotifyActivity.this, LocationActivity.class);
            startActivity(intent);

        }else if(item.toString().contains("Logout")){
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(NotifyActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void facebookPost(){
        Log.d("asdasd","asdasd");
        CallbackManager callbackManager;
        final ShareDialog shareDialog = new ShareDialog(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, new

                FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {}

                    @Override
                    public void onCancel() {}

                    @Override
                    public void onError(FacebookException error) {}
                });
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("EventIn")
                    .setContentDescription("Hi guys! try out the new EventIn app")

                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .build();

            shareDialog.show(linkContent);
        }

    }
    public void sendsms(){
        Log.d("asdasd","asasdasdasd");

        Intent intent = new Intent(NotifyActivity.this, ContactActivity.class);

        intent.putExtra("eventTitle",eventTitle);
        startActivity(intent);
    }
}
