package com.sdsmdg.bookshareapp.BSA.ui;

import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout;
    private TextInputEditText emailInputEditText;
    private Button submitButton;
    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailInputEditText.getText().toString().trim().length() != 0){
                    emailInputLayout.setErrorEnabled(false);
                    sendEmail(emailInputEditText.getText().toString());
                } else{
                    emailInputLayout.setErrorEnabled(true);
                    emailInputLayout.setError("Please enter an email address!!");
                }
            }
        });
    }

    private void sendEmail(String email) {
        customProgressDialog.show();
        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<ResponseBody> sendEmailResponse = usersAPI.sendMail(email);
        sendEmailResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                customProgressDialog.dismiss();
                if (response.isSuccessful()){
                    emailInputLayout.setEnabled(false);
                    Toast.makeText(ForgotPasswordActivity.this, "An E-mail has been sent to you", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 500);
                } else{
                    emailInputLayout.setEnabled(true);
                    Toast.makeText(ForgotPasswordActivity.this, "Invalid email address!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                customProgressDialog.dismiss();
                emailInputLayout.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initViews() {
        emailInputLayout = (TextInputLayout) findViewById(R.id.email_edit_text);
        emailInputEditText = (TextInputEditText) findViewById(R.id.txt_email_address);
        submitButton = (Button) findViewById(R.id.submit_email_button);
        customProgressDialog = new CustomProgressDialog(this);
        customProgressDialog.setCancelable(false);
    }
}
