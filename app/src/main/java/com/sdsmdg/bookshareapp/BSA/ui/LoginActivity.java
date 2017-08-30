package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;
import com.sdsmdg.bookshareapp.BSA.api.models.Login;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    SharedPreferences pref, prevEmail;//The prevEmail preference is used to store the last two email of the user for the suggestion

    @InjectView(R.id.input_email)
    AutoCompleteTextView _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id._btn_show_password)
    ImageButton _showPassword;
    @InjectView(R.id.btn_login)
    Button _loginButton;
    @InjectView(R.id.link_signup)
    TextView _signupLink;
    @InjectView(R.id.link_forgot_password)
    TextView forgotPasswordLink;
    String token;
    Context context;
    boolean showPassword = false;
    CustomProgressDialog customProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        context = this;
        // underline the forget password text view
        forgotPasswordLink.setPaintFlags(forgotPasswordLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(_emailText.getWindowToken(), 0);

        pref = getApplicationContext().getSharedPreferences("Token", MODE_PRIVATE);
        prevEmail = getApplicationContext().getSharedPreferences("Previous Email", MODE_PRIVATE);
        String emails[];
        if (prevEmail.getString("email1", null) == null) {
            emails = new String[1];
            emails[0] = prevEmail.getString("email2", null);
        } else if (prevEmail.getString("email2", null) == null) {
            emails = new String[1];
            emails[0] = prevEmail.getString("email1", null);
        } else {
            emails = new String[2];
            emails[0] = prevEmail.getString("email1", null);
            emails[1] = prevEmail.getString("email2", null);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, emails);
        _emailText.setAdapter(adapter);

        token = pref.getString("token", "");
        _showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showPassword) {
                    _passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPassword = true;
                    _passwordText.setSelection(_passwordText.getText().length());
                    _showPassword.setImageResource(R.drawable.ic_visible_off);
                } else {
                    _passwordText.setInputType(129); //input type = password
                    showPassword = false;
                    _passwordText.setSelection(_passwordText.getText().length());
                    _showPassword.setImageResource(R.drawable.ic_visible_on);
                }
            }

        });

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void guestModeClicked(View view) {
        Intent i = new Intent(this, GuestActivity.class);
        startActivity(i);
    }

    public void signUpClicked(View view) {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }

    public void login() {
        if (!validate()) {
            onLoginFailed("Fill complete details!");
            return;
        }

        //_loginButton.setEnabled(false);

        customProgressDialog = new CustomProgressDialog(LoginActivity.this);
        customProgressDialog.setCancelable(false);
        customProgressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        Helper.setUserEmail(email);

        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Login> call = usersAPI.getToken(email, password);
        call.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.body() != null) {
                    if (response.body().getDetail() != null) {
                        onLoginFailed(response.body().getDetail());
                    }
                    if (response.body().getToken() != null) {
                        setNewEmail(_emailText.getText().toString());//This function sets the new entered email into the shared prefs for suggestions
                        onLoginSuccess();
                        saveinSP(response.body().getToken(), response.body().getUserInfo());
                    }
                }
                customProgressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                onLoginFailed("Check your network connectivity and try again!");
                t.printStackTrace();
                customProgressDialog.dismiss();
            }
        });
    }

    public void setNewEmail(String email) {

        String email2 = prevEmail.getString("email2", null);

        String email1 = email2;
        if (!email.equals(email1)) {
            email2 = email;
        } else {
            email2 = null;
        }

        SharedPreferences.Editor editor = prevEmail.edit();
        editor.putString("email1", email1);
        editor.putString("email2", email2);
        editor.apply();
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
        i.putExtra("data_login", "update");
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
        editor.apply();

    }
}
