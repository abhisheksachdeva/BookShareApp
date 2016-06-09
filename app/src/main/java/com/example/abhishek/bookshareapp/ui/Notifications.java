package com.example.abhishek.bookshareapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.abhishek.bookshareapp.R;

public class Notifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

    }

    @Override
    public void onBackPressed() {
        Intent i= new Intent(this,MainActivity.class);
        startActivity(i);
        finish();

    }
}
