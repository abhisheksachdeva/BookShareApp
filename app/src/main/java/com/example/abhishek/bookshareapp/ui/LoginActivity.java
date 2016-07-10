package com.example.abhishek.bookshareapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhishek.bookshareapp.R;
import com.example.abhishek.bookshareapp.api.NetworkingFactory;
import com.example.abhishek.bookshareapp.api.UsersAPI;
import com.example.abhishek.bookshareapp.api.models.Login;
import com.example.abhishek.bookshareapp.api.models.UserInfo;
import com.example.abhishek.bookshareapp.utils.Helper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    SharedPreferences pref;

    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id._btn_show_password)
    ImageButton _showPassword;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;

    String token;
    Context context;
    boolean showPassword = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        context = this;

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(_emailText.getWindowToken(), 0);

        pref = getApplicationContext().getSharedPreferences("Token", MODE_PRIVATE);
        token = pref.getString("token", "");
        Log.i("harshit", token + "  adf");

        _showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!showPassword){
                _passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPassword=true;
                    _showPassword.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    _passwordText.setInputType(129); //input type = password
                    showPassword=false;
                    _showPassword.setImageResource(R.drawable.ic_visibility);
                }
                }

        });

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("Fill complete details!");
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString() + "@iitr.ac.in";
        String password = _passwordText.getText().toString();
        Helper.setUserEmail(email);

        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Login> call = usersAPI.getToken(email, password);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.body() != null) {
                    if(response.body().getDetail() != null) {
                        onLoginFailed(response.body().getDetail());
                    }
                    if(response.body().getToken() != null) {
                        onLoginSuccess();
                        saveinSP(response.body().getToken(), response.body().getUserInfo());
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                Log.i(TAG, "onFailure: called");
                onLoginFailed("Check your network connectivity and try again!");
                progressDialog.dismiss();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // By default we just finish the Activity and log them in automatically
                Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            _passwordText.setError("between 6 and 15 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void saveinSP(String token, UserInfo userInfo) {

        pref = getSharedPreferences("Token", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.putString("id", userInfo.getId());
        editor.putString("email", userInfo.getEmail());
        editor.putString("first_name", userInfo.getFirstName());
        editor.putString("last_name", userInfo.getLastName());
        editor.putString("hostel", userInfo.getHostel());
        editor.putString("room_no", userInfo.getRoomNo());
        editor.putString("contact_no", userInfo.getContactNo());
        editor.putString("enr_no", userInfo.getEnrNo());
        editor.putString("college", userInfo.getCollege());

        Log.i("room_no", userInfo.getRoomNo());
        editor.apply();

    }
}
