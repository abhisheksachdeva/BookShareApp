package com.example.abhishek.bookshareapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.LocalBooks.Book;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.ui.adapter.LocalBooksAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfile extends AppCompatActivity {
    TextView userName, userEmail, address;
    UserInfo user;
    List<Book> booksList;
    LocalBooksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        userName = (TextView) findViewById(R.id.username);
        userEmail = (TextView) findViewById(R.id.useremail);
        address = (TextView) findViewById(R.id.address);

        String id = getIntent().getExtras().getString("id");

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
    }

    public void myBooks(View view) {
        Intent i = new Intent(this, MyBooks.class);
        startActivity(i);
    }

    public void changePassword(View view) {
        Intent i = new Intent(this, ChangePasswordActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
