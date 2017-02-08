package com.sdsmdg.bookshareapp.BSA.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.Signup;
import com.sdsmdg.bookshareapp.BSA.api.otp.MSGApi;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.VerifyOtpFragment;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity implements VerifyOtpFragment.OnOTPVerifyListener {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_Fname)
    EditText _FnameText;
    @InjectView(R.id.input_Lname)
    EditText _LnameText;
    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.input_cnf_password)
    EditText _cnf_passwordText;
    @InjectView(R.id.input_room_no)
    EditText _roomText;
    @InjectView(R.id.input_roll_no)
    EditText _rollText;
    @InjectView(R.id.hostel_spinner)
    Spinner _hostelSpinner;
    @InjectView(R.id.input_college)
    EditText _collegeText;
    @InjectView(R.id.input_contact)
    EditText _contactText;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;
    String hostel;
    @InjectView(R.id._btn_show_password)
    ImageButton _showPassword;
    @InjectView(R.id._btn_show_cnf_password)
    ImageButton _showCnfPassword;
    boolean showPassword = false, showCnfPassword = false;

    String generatedOTP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        //////////////// Setting spinner for hostels \\\\\\\\\\\\\\\\\
        ArrayAdapter<CharSequence> hostelAdapter = ArrayAdapter.createFromResource(this, R.array.hostel_list, android.R.layout.simple_spinner_item);

        hostelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _hostelSpinner.setAdapter(hostelAdapter);

        _hostelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hostel = parent.getItemAtPosition(position).toString();
//                hideKeyboard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                hostel = "Azad";
            }
        });
        /////////////////////////// Spinner complete \\\\\\\\\\\\\\\\\\\\\\\\\\\\

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup(hostel);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        _showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showPassword) {
                    _passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPassword = true;
                    _showPassword.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    _passwordText.setInputType(129); //input type = password
                    showPassword = false;
                    _showPassword.setImageResource(R.drawable.ic_visibility);
                }
            }

        });
        _showCnfPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showCnfPassword) {
                    _cnf_passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showCnfPassword = true;
                    _showCnfPassword.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    _cnf_passwordText.setInputType(129); //input type = password
                    showCnfPassword = false;
                    _showCnfPassword.setImageResource(R.drawable.ic_visibility);
                }
            }

        });
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void signup(String hostel) {

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
        String email = _emailText.getText().toString() + "@iitr.ac.in";
        String password = _passwordText.getText().toString();
        String room_no = _roomText.getText().toString();
        String roll_no = _rollText.getText().toString();
        String college = _collegeText.getText().toString();
        final String contact = _contactText.getText().toString();

        //If the contact no. is empty, sign up directly, else, open the otp dialog to verify the entered contact no.
        if (!contact.equals("")) {
            sendOTP(contact);
        } else {
            requestSignUp(fname, lname, email, password, room_no, roll_no, college, contact);
        }

        progressDialog.dismiss();
    }

    private void requestSignUp(String fname, String lname, String email, String password, String room_no, String roll_no, String college, String contact) {
        Helper.setUserEmail(email);
        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Signup> userInfoCall = usersAPI.getUserInfo(email, college, hostel, room_no, roll_no, fname, lname, contact, FirebaseInstanceId.getInstance().getToken(), password);
        Log.i("FCM_token ", FirebaseInstanceId.getInstance().getToken() + " <-");
        userInfoCall.enqueue(new retrofit2.Callback<Signup>() {

            @Override
            public void onFailure(Call<Signup> call, Throwable t) {
                onSignupFailed("Check your network connection properly");
            }

            @Override
            public void onResponse(Call<Signup> call, Response<Signup> response) {
                if (response.body() != null) {
                    String detail = response.body().getDetail();

                    if (detail.equals("Fill required details or Email id already registered.")) {
                        onSignupFailed("Email already registered" + FirebaseInstanceId.getInstance().getToken());
                    } else {
                        //If the user has not entered his phone no. complete the signup, else show enter otp dialog
                        onSignupSuccess();
                    }
                }
            }
        });
    }

    public void sendOTP(String contact) {

        String generatedOTP = generateOTP();
        sendMessage("Your OTP for citadel is " + generatedOTP, contact);

        Bundle bundle = new Bundle();
        bundle.putString("generated_otp", generatedOTP);

        DialogFragment dialog = new VerifyOtpFragment();
        dialog.setArguments(bundle);

        dialog.show(getSupportFragmentManager(), "tagOTP");
    }

    public void sendMessage(String message, String contactNo) {

        //Change the contact no. according to the need of otp api
        contactNo = "91" + contactNo;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();

        client.interceptors().add(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://control.msg91.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();

        MSGApi api = retrofit.create(MSGApi.class);
        Call<com.sdsmdg.bookshareapp.BSA.api.otp.Models.Response> call = api.sendOTP(
                CommonUtilities.MSG_AUTH_KEY,
                contactNo,
                message,
                "CITADL",
                4,
                91,
                "json"
        );

        call.enqueue(new Callback<com.sdsmdg.bookshareapp.BSA.api.otp.Models.Response>() {
            @Override
            public void onResponse(Call<com.sdsmdg.bookshareapp.BSA.api.otp.Models.Response> call, Response<com.sdsmdg.bookshareapp.BSA.api.otp.Models.Response> response) {
                if (response.body().getType().equals("success")) {
                    Toast.makeText(getApplicationContext(), "OTP sent", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, response.body().getType());
                    Log.i(TAG, response.body().getMessage());
                    Toast.makeText(getApplicationContext(), "OTP not sent", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.sdsmdg.bookshareapp.BSA.api.otp.Models.Response> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "OTP failed to send", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String generateOTP() {
        generatedOTP = String.valueOf((int) (1000 + Math.random() * 8999));
        return generatedOTP;
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
        String college = _collegeText.getText().toString();
        String contact = _contactText.getText().toString();

        if (fname.isEmpty()) {
            valid = false;
            _FnameText.setError("Please fill First Name");

        } else {
            _FnameText.setError(null);
        }
        if (lname.isEmpty()) {
            valid = false;
            _FnameText.setError("Please fill last Name");

        } else {
            _LnameText.setError(null);
        }
        if (room_no.isEmpty()) {
            valid = false;
            _FnameText.setError("Please fill Room no");

        } else {
            _roomText.setError(null);

        }
        if (roll_no.isEmpty()) {
            valid = false;
            _FnameText.setError("Please fill Roll no");

        } else {
            _rollText.setError(null);
        }
        if (college.isEmpty()) {
            valid = false;
            _FnameText.setError("Please fill college Name");

        } else {
            _collegeText.setError(null);
        }
        if (contact.isEmpty()) {
            valid = false;
            _FnameText.setError("Please fill contact Name");

        } else {
            _contactText.setError(null);
        }

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
        if (cnf_password.isEmpty() || !(cnf_password.equals(password))) {
            if (cnf_password.isEmpty())
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

    @Override
    public void onOTPVerified() {

        String fname = _FnameText.getText().toString();
        String lname = _LnameText.getText().toString();
        String email = _emailText.getText().toString() + "@iitr.ac.in";
        String password = _passwordText.getText().toString();
        String room_no = _roomText.getText().toString();
        String roll_no = _rollText.getText().toString();
        String college = _collegeText.getText().toString();
        String contact = _contactText.getText().toString();

        //As the otp is verified now, the user signs up with his correct no. in the database
        requestSignUp(fname, lname, email, password, room_no, roll_no, college, contact);
    }
}
