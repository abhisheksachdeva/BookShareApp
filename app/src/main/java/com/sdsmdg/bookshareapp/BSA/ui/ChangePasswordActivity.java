package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences("Token", MODE_PRIVATE);
        id = prefs.getString("id", null);
        token = prefs.getString("token", null);

        newPasswordInput = (EditText) findViewById(R.id.new_password);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
        }

        return (super.onOptionsItemSelected(item));
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
                        }
                        //The request is not valid when the token provided by the user is not correct
                        else {
                            Toast.makeText(getApplicationContext(), "Request not valid", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Detail> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MyProfile.class);
        i.putExtra("id", prefs.getString("id", prefs.getString("id", "")));
        startActivity(i);
    }
}
