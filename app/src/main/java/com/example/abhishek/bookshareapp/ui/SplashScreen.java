package com.example.abhishek.bookshareapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.example.abhishek.bookshareapp.R;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread timer= new Thread(){

            public void run(){

                try{
                    sleep(5000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally{
                    Intent opensp = new Intent(SplashScreen.this,LoginActivity.class);
                    startActivity(opensp);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}