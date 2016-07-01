package com.example.abhishek.bookshareapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.klinker.android.sliding.SlidingActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends SlidingActivity {
    TextView userName, userEmail, address;
    UserInfo user;
    String id;

    @Override
    public void init(Bundle savedInstanceState) {
        setTitle("My Profile");
        setPrimaryColors(
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
        );
        setContent(R.layout.activity_my_profile);
        setImage(R.drawable.default_profile_pic);
        userName = (TextView) findViewById(R.id.username);
        userEmail = (TextView) findViewById(R.id.useremail);
        address = (TextView) findViewById(R.id.address);

        id = getIntent().getExtras().getString("id");
        getUserInfoDetails(id);
    }


    public void getUserInfoDetails(String id) {
        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<UserInfo> call = api.getUserDetails(id);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.body() != null) {
                    Log.d("UserProfile Response:", response.toString());
                    user = response.body();
                    userName.setText(user.getFirstName() + " " + user.getLastName());
                    userEmail.setText(user.getEmail());
                    address.setText(user.getRoomNo() + ", " + user.getHostel());

                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());
            }
        });
    }

    public void editProfile(View view) {
        Intent i = new Intent(this, EditProfileActivity.class);
        startActivity(i);
        finish();
    }

    public void myBooks(View view) {
        Intent i = new Intent(this, MyBooks.class);
        startActivity(i);

    }

    public void changePassword(View view) {
        Intent i = new Intent(this, ChangePasswordActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
