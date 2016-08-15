package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends Activity {
    String token;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        pref = getApplicationContext().getSharedPreferences("Token", MODE_PRIVATE);
        token = pref.getString("token", null);

        if(isOnline()) {
            verifyToken();
        } else {
            Toast.makeText(SplashScreen.this, "Check network connectivity and try again", Toast.LENGTH_SHORT).show();
            Handler h = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            };
            h.postDelayed(r, 1500);
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void verifyToken() {

        if (token != null) {

            UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
            Call<Detail> call = usersAPI.getUserEmail("Token "+token);
            call.enqueue(new Callback<Detail>() {
                @Override
                public void onResponse(Call<Detail> call, Response<Detail> response) {

                    if (response.body() != null) {
                        if (response.body().getDetail() != null) {
                            if (!response.body().getDetail().equals("")) {

                                Helper.setUserEmail(response.body().getDetail());
                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(SplashScreen.this, "Failed to log in due to internal error!", Toast.LENGTH_SHORT).show();
                                try {
                                    Thread.sleep(1000);
                                } catch(InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }
                    } else {
                        Log.i("harshit", "response.body() is null");
                        Toast.makeText(SplashScreen.this, "Failed to log in due to internal error!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Detail> call, Throwable t) {
                    Toast.makeText(SplashScreen.this, "Login failed", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }
        else {
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }

    }

}