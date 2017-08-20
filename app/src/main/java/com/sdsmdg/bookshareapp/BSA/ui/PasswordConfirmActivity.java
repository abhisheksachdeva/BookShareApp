package com.sdsmdg.bookshareapp.BSA.ui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordConfirmActivity extends AppCompatActivity {

    private TextView helperTextView;
    private TextInputLayout newPasswordInputLayout, confirmPasswordInputLayout;
    private TextInputEditText newPasswordEditText, confirmPasswordEditText;
    private Button submitButton;
    private Uri data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirm);
        initViews();
        data = getIntent().getData();

        newPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = newPasswordEditText.getText().toString();
                if (TextUtils.isEmpty(password) || password.length() < 8){
                    helperTextView.setTextColor(Color.RED);
                } else{
                    helperTextView.setTextColor(ContextCompat.getColor
                            (PasswordConfirmActivity.this, R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPasswordEditText.getText().length() < 8){
                    newPasswordInputLayout.setErrorEnabled(true);
                    confirmPasswordInputLayout.setErrorEnabled(false);
                    newPasswordInputLayout.setError("The password must be of atleast 8 characters");
                } else if(!newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
                    newPasswordInputLayout.setErrorEnabled(false);
                    confirmPasswordInputLayout.setErrorEnabled(true);
                    confirmPasswordInputLayout.setError("The passwords do not match");
                } else {
                    newPasswordInputLayout.setEnabled(false);
                    confirmPasswordInputLayout.setEnabled(false);
                    resetPassword();
                }
            }
        });
    }

    private void resetPassword() {
        String[] segments = data.getPath().split("/");
        UsersAPI usersApi = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<ResponseBody> confirmPasswordCall = usersApi.confirmPassword(
                segments[2], segments[3],
                newPasswordEditText.getText().toString(),
                confirmPasswordEditText.getText().toString());
        confirmPasswordCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(PasswordConfirmActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 500);
                } else{
                    Toast.makeText(PasswordConfirmActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initViews() {
        helperTextView = (TextView) findViewById(R.id.txt_reset_password_message);
        newPasswordInputLayout = (TextInputLayout) findViewById(R.id.new_password_edit_text_layout);
        confirmPasswordInputLayout = (TextInputLayout) findViewById(R.id.cnf_password_edit_text_layout);
        newPasswordEditText =  (TextInputEditText) findViewById(R.id.new_pwd_edit_text);
        confirmPasswordEditText = (TextInputEditText) findViewById(R.id.cnf_pwd_edit_text);
        submitButton = (Button) findViewById(R.id.reset_pwd_button);
    }
}
