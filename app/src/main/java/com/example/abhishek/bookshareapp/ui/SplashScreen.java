package com.example.abhishek.bookshareapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.VerifyToken.Detail;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;
import com.example.abhishek.bookshareapp.utils.Helper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        verifyToken();

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
                    Toast.makeText(SplashScreen.this, "Check network connectivity and try again", Toast.LENGTH_SHORT).show();
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