package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

    String token;
    SharedPreferences prefs;
    private TextInputLayout oldPasswordInputLayout, newPasswordInputLayout;
    private TextInputEditText oldPasswordEditText, newPasswordEditText;
    private Button submitButton;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences("Token", MODE_PRIVATE);
        token = prefs.getString("token", null);

        initViews();
        regListeners();
    }

    private void regListeners() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPasswordEditText.getText().length() < 6) {
                    oldPasswordInputLayout.setErrorEnabled(true);
                    newPasswordInputLayout.setErrorEnabled(false);
                    oldPasswordInputLayout.setError("The password must be of atleast 6 characters");
                } else if (newPasswordEditText.getText().length() < 6) {
                    oldPasswordInputLayout.setErrorEnabled(false);
                    newPasswordInputLayout.setErrorEnabled(true);
                    newPasswordInputLayout.setError("The password must be of atleast 6 characters");
                } else {
                    oldPasswordInputLayout.setEnabled(false);
                    newPasswordInputLayout.setEnabled(false);
                    View focusable = getCurrentFocus();
                    if (focusable != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(focusable.getWindowToken(), 0);
                    }
                    onSaveClicked();
                }
            }
        });
    }

    private void initViews() {
        oldPasswordInputLayout = (TextInputLayout) findViewById(R.id.old_password_edit_text_layout);
        newPasswordInputLayout = (TextInputLayout) findViewById(R.id.new_password_edit_text_layout);
        oldPasswordEditText = (TextInputEditText) findViewById(R.id.old_pwd_edit_text);
        newPasswordEditText = (TextInputEditText) findViewById(R.id.new_pwd_edit_text);
        submitButton = (Button) findViewById(R.id.save);
        progressDialog = new CustomProgressDialog(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    public void onSaveClicked() {
        progressDialog.show();

        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();

        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Detail> call = usersAPI.changePassword(
                "Token " + token,
                oldPassword,
                newPassword
        );
        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                progressDialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getDetail().equals("Password changed")) {
                        Toast.makeText(ChangePasswordActivity.this,
                                "Password changed", Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 500);
                    }
                    //The request is not valid when the password provided by the user is not correct
                    else {
                        oldPasswordInputLayout.setEnabled(true);
                        newPasswordInputLayout.setEnabled(true);
                        Toast.makeText(ChangePasswordActivity.this,
                                "The password entered is incorrect!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    oldPasswordInputLayout.setEnabled(true);
                    newPasswordInputLayout.setEnabled(true);
                    Toast.makeText(ChangePasswordActivity.this,
                            R.string.connection_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                oldPasswordInputLayout.setEnabled(true);
                newPasswordInputLayout.setEnabled(true);
                progressDialog.dismiss();
                Toast.makeText(ChangePasswordActivity.this,
                        R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
