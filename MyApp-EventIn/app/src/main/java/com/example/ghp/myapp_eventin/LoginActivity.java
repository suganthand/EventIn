package com.example.ghp.myapp_eventin;

/**
 * Created by ghp on 31-Jul-16.
 */

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.Locale;


public class LoginActivity extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private CallbackManager mCallbackManager;
    private Integer image = R.drawable.front_image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("dfdf","dfdf");
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();
        Log.d("1111111",mCallbackManager.toString());
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        Intent myIntent = new Intent(LoginActivity.this, CategoryActivity.class);
                        startActivity(myIntent);

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }


                });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        updateWithToken(AccessToken.getCurrentAccessToken());

        setContentView(R.layout.activity_login);
        final ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
        imageView.setImageResource(image);


//        Button btn_fb_login = (Button)findViewById(R.id.btn_fb_login);
//
//        btn_fb_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends"));
////                Log.d()
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }
    public void startTimer(View view){
        Log.d("dfsdf","sfsf");
        LoginManager.getInstance().logOut();

    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
//                    Intent i = new Intent(SplashScreen.this, GeekTrivia.class);
//                    startActivity(i);
                    Intent myIntent = new Intent(LoginActivity.this, CategoryActivity.class);
                    startActivity(myIntent);
                    Log.d("ifffff","iffff");
//                    finish();
                }
            }, 1000);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
//                    Intent i = new Intent(SplashScreen.this, Login.class);
//                    startActivity(i);
//                    Intent myIntent = new Intent(LoginActivity.this, LoginActivity.class);
//                    startActivity(myIntent);

                    Log.d("elseeeee","elseeee");
//                    finish();
                }
            }, 1000);
        }
    }
}
