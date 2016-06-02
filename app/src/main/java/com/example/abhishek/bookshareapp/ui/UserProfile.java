package com.example.abhishek.bookshareapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.abhishek.bookshareapp.R;

public class UserProfile extends AppCompatActivity {
    TextView userName,userEmail,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = (TextView)findViewById(R.id.username);
        userEmail = (TextView)findViewById(R.id.useremail);
        address = (TextView)findViewById(R.id.address);

        String user = getIntent().getExtras().getString("user");
        String email = getIntent().getExtras().getString("email");
        String hostel = getIntent().getExtras().getString("hostel");
        String room = getIntent().getExtras().getString("room");

        userName.setText("Name : "+user);
        address.setText("Address : "+room+" "+hostel);
        userEmail.setText("Email Address : "+email);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
