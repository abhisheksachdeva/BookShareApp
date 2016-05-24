package com.example.abhishek.bookshareapp.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.SignUp.UserInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_Fname) EditText _FnameText;
    @InjectView(R.id.input_Lname) EditText _LnameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_cnf_password) EditText _cnf_passwordText;
    @InjectView(R.id.input_room_no) EditText _roomText;
    @InjectView(R.id.input_roll_no) EditText _rollText;
    @InjectView(R.id.input_hostel) EditText _hostelText;
    @InjectView(R.id.input_college) EditText _collegeText;
    @InjectView(R.id.input_contact) EditText _contactText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("Fill details properly");
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String fname = _FnameText.getText().toString();
        String lname = _LnameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String cnf_password = _cnf_passwordText.getText().toString();
        String room_no = _roomText.getText().toString();
        String roll_no = _rollText.getText().toString();
        String hostel = _hostelText.getText().toString();
        String college = _collegeText.getText().toString();
        String contact = _contactText.getText().toString();

        OkHttpClient.Builder httpclient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.43.80:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpclient.build())
                .build();

        UsersAPI usersAPI = retrofit.create(UsersAPI.class);

        Call<UserInfo> userInfoCall = usersAPI.get(email, college, hostel, room_no, roll_no, fname, lname, contact, password);

        userInfoCall.enqueue(new retrofit2.Callback<UserInfo>() {

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                onSignupFailed("Check your network connection properly");
            }

            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                UserInfo userInfo = response.body();

                if(userInfo.getEmail()!=null) {
                    Log.i("harshit", userInfo.getEmail());
                    onSignupSuccess();
                }
                else {
                    onSignupFailed("Fill details completely");
                }
            }
        });

        progressDialog.dismiss();
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed(String toast) {
        Toast.makeText(getBaseContext(), toast, Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String fname = _FnameText.getText().toString();
        String lname = _LnameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String cnf_password = _cnf_passwordText.getText().toString();
        String room_no = _roomText.getText().toString();
        String roll_no = _rollText.getText().toString();
        String hostel = _hostelText.getText().toString();
        String college = _collegeText.getText().toString();
        String contact = _contactText.getText().toString();

        if (fname.isEmpty() ) {
            valid = false;
        } else {
            _FnameText.setError(null);
        }
        if (lname.isEmpty() ) {
            valid = false;
        } else {
            _LnameText.setError(null);
        }
        if (room_no.isEmpty() ) {
            valid = false;
        } else {
            _roomText.setError(null);
        }
        if (roll_no.isEmpty() ) {
            valid = false;
        } else {
            _rollText.setError(null);
        }
        if (hostel.isEmpty() ) {
            valid = false;
        } else {
            _hostelText.setError(null);
        }
        if (college.isEmpty() ) {
            valid = false;
        } else {
            _collegeText.setError(null);
        }
        if (contact.isEmpty() ) {
            valid = false;
        } else {
            _contactText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        if (cnf_password.isEmpty() || !(cnf_password.equals(password))) {
            if(cnf_password.isEmpty())
                _cnf_passwordText.setError("Please enter password");
            else {
                _cnf_passwordText.setError("Please enter same password");
            }
            valid = false;
        } else {
            _cnf_passwordText.setError(null);
        }

        return valid;
    }
}
