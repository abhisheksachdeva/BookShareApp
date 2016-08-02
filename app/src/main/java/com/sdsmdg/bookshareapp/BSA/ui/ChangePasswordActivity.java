package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    String id;
    String token;
    SharedPreferences prefs;
    EditText newPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        prefs = getSharedPreferences("Token", MODE_PRIVATE);
        id = prefs.getString("id", null);
        token = prefs.getString("token", null);

        newPasswordInput = (EditText) findViewById(R.id.new_password);
    }

    public void onSaveClicked(View view) {
        String password = newPasswordInput.getText().toString();
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            newPasswordInput.setError("between 4 and 10 alphanumeric characters");
        } else {
            UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
            Call<Detail> call = usersAPI.changePassword(
                    id,
                    token,
                    password
            );
            call.enqueue(new Callback<Detail>() {
                @Override
                public void onResponse(Call<Detail> call, Response<Detail> response) {
                    if (response.body() != null) {
                        if (response.body().getDetail().equals("Password changed")) {
                            Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Request not valid", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.i("CPA", "request body is null");
                    }
                }

                @Override
                public void onFailure(Call<Detail> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Check internet connectivity and try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i= new Intent(this,MyProfile.class);
        i.putExtra("id", prefs.getString("id", prefs.getString("id", "")));
        startActivity(i);
    }
}
