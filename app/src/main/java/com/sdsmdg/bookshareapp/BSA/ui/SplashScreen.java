package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import java.util.Iterator;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends Activity {

    String token;
    String extra_data = "none";
    SharedPreferences pref, firstLaunchSharedPreferences;
    SharedPreferences.Editor editor;
    private static final String FIRST_LAUNCH_PREF_NAME = "is_first_launch";
    private static final String IS_FIRST_TIME_LAUNCH = "is_first_time_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firstLaunchSharedPreferences = getSharedPreferences(FIRST_LAUNCH_PREF_NAME, MODE_PRIVATE);

        pref = getApplicationContext().getSharedPreferences("Token", MODE_PRIVATE);
        token = pref.getString("token", null);

        if (getIntent().getExtras() != null) {
            String data = getIntent().getExtras().getString("google.message_id");
            if (data != null) {
                extra_data = "open_drawer";
            }
            dumpIntent(getIntent());
        }

        if (isOnline()) {
            verifyToken();
        } else if (token != null) {
            if (isFirstTimeLaunch()){
                launchFirstTime("data_splash", extra_data);
            } else {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.putExtra("data_splash", extra_data);
                startActivity(intent);
                finish();
            }
        } else{
            if (isFirstTimeLaunch()){
                launchFirstTime("toast_message", "There is no internet connection!");
            } else {
                Toast.makeText(SplashScreen.this, "There is no internet connection!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    /**
     * Method to launch the welcome activity
     * @param intentTag the tag associated with the intent
     * @param intentData the toReadName associated with the intent
     */
    private void launchFirstTime(String intentTag, String intentData) {
        editor = firstLaunchSharedPreferences.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, false);
        editor.apply();
        Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
        intent.putExtra(intentTag, intentData);
        startActivity(intent);
        finish();
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
            Call<Detail> call = usersAPI.getUserEmail("Token " + token);
            call.enqueue(new Callback<Detail>() {
                @Override
                public void onResponse(Call<Detail> call, Response<Detail> response) {
                    if (response.body() != null) {
                        if (response.body().getDetail() != null) {
                            if (!response.body().getDetail().equals("")) {
                                Helper.setUserEmail(response.body().getDetail());
                                if (isFirstTimeLaunch()){
                                    launchFirstTime("data_splash", extra_data);
                                } else{
                                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                    intent.putExtra("data_splash", extra_data);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (isFirstTimeLaunch()){
                                    launchFirstTime("toast_message", "Failed to Log in");
                                }else {
                                    Toast.makeText(SplashScreen.this, "Failed to Log in", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    } else {
                        if (isFirstTimeLaunch()){
                            launchFirstTime("toast_message", "Failed to Log in");
                        }else {
                            Toast.makeText(SplashScreen.this, "Failed to Log in", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Detail> call, Throwable t) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isFirstTimeLaunch()){
                        launchFirstTime("toast_message", "Failed to Log in");
                    }else {
                        Toast.makeText(SplashScreen.this, "Failed to Login", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        } else {
            if (isFirstTimeLaunch()){
                editor = firstLaunchSharedPreferences.edit();
                editor.putBoolean(IS_FIRST_TIME_LAUNCH, false);
                editor.apply();
                Handler h = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                };
                h.postDelayed(r, 1500);
            }else {
                Handler h = new Handler();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                };
                h.postDelayed(r, 1500);
            }
        }

    }

    public static void dumpIntent(Intent i) {

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
               // Log.e("DUMP INTENT", "[" + key + "=" + bundle.get(key) + "]");
            }

        }
    }

    /**
     * to check whether the app is opened for the first time
     * @return boolean telling whether the app is opened first
     */
    public boolean isFirstTimeLaunch() {
        return firstLaunchSharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}