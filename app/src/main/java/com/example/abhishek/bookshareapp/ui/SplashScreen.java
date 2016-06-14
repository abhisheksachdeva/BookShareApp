package com.example.abhishek.bookshareapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.VerifyToken.UserEmail;
import com.example.abhishek.bookshareapp.utils.CommonUtilities;
import com.example.abhishek.bookshareapp.utils.Helper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SplashScreen extends Activity {
    String token;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        pref = getApplicationContext().getSharedPreferences("Token", MODE_PRIVATE);
        token = pref.getString("token", "");
        Log.i("SS", token + "  added");

        Thread timer= new Thread(){

            public void run(){

                try{
                    sleep(5000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally{
                    verifyToken();
                    Intent opensp = new Intent(SplashScreen.this,LoginActivity.class);
                    startActivity(opensp);
                }
            }
        };
        timer.start();
    }

    public void verifyToken() {

        if (token != null && !token.equals("")) {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request request = chain.request().newBuilder().
                                    addHeader("Authorization", "Token " + token).build();
                            return chain.proceed(request);
                        }
                    }).build();

            Retrofit retrofit = new Retrofit.Builder().
                    addConverterFactory(GsonConverterFactory.create()).
                    baseUrl(CommonUtilities.local_books_api_url).
                    client(httpClient).
                    build();

            UsersAPI usersAPI = retrofit.create(UsersAPI.class);
            Call<UserEmail> call = usersAPI.getUserEmail();
            call.enqueue(new Callback<UserEmail>() {
                @Override
                public void onResponse(Call<UserEmail> call, Response<UserEmail> response) {

                    if(response.body() != null) {
                        if (response.body().getEmail() != null) {
                            if (!response.body().getEmail().equals("")) {

                                Helper.setUserEmail(response.body().getEmail());
                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(SplashScreen.this, "Failed to log in due to internal error!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    } else {
                        Log.i("harshit", "response.body() is null");
                    }
                }
                @Override
                public void onFailure(Call<UserEmail> call, Throwable t) {
                    Toast.makeText(SplashScreen.this, "Check network connectivity and try again", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}