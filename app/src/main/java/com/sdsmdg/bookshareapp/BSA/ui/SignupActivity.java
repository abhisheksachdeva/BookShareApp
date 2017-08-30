package com.sdsmdg.bookshareapp.BSA.ui;


import android.content.Context;
import android.content.SharedPreferences;
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
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.CollegeAdapter;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.VerifyOtpFragment;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import java.util.ArrayList;
import java.util.List;

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
    @InjectView(R.id.domain_spinner)
    Spinner _domainSpinner;
    @InjectView(R.id.text_college_domain)
    TextView domainTextView;
    ArrayAdapter<CharSequence> hostelAdapter;
    String domain = "@iitr.ac.in";
    ArrayList<College> colleges;
    int hostelResId = R.array.iitr_hostel_list;
    boolean showPassword = false, showCnfPassword = false;
    String generatedOTP;
    CustomProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

//        _emailText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
//                new TextDrawable("hello", 0), null);

        colleges = new ArrayList<>();
        addColleges();
        CollegeAdapter collegeAdapter = new CollegeAdapter(getApplicationContext(),colleges);
        _domainSpinner.setAdapter(collegeAdapter);

        _domainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                College college = (College) parent.getItemAtPosition(position);
                domain = college.getCollegeDomain();
                _collegeText.setText(college.getCollegeName());
                domainTextView.setText(college.getCollegeDomain());
                getHostels(college.getCollegeName());
                setHostelSpinner();
                writeSharedPreferences();
                hostelAdapter.notifyDataSetChanged();
                _collegeText.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                domain = "@iitr.ac.in";
                _collegeText.setText(getResources().getString(R.string.iitr));
                hostelResId = R.array.iitr_hostel_list;
            }
        });

        //Setting spinner for hostels
        setHostelSpinner();
        writeSharedPreferences();

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
                    _passwordText.setSelection(_passwordText.getText().length());
                    showPassword = true;
                    _showPassword.setImageResource(R.drawable.ic_visible_off);
                } else {
                    _passwordText.setInputType(129); //input type = password
                    showPassword = false;
                    _passwordText.setSelection(_passwordText.getText().toString().length());
                    _showPassword.setImageResource(R.drawable.ic_visible_on);
                }
            }

        });
        _showCnfPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showCnfPassword) {
                    _cnf_passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showCnfPassword = true;
                    _cnf_passwordText.setSelection(_cnf_passwordText.getText().toString().length());
                    _showCnfPassword.setImageResource(R.drawable.ic_visible_off);
                } else {
                    _cnf_passwordText.setInputType(129); //input type = password
                    showCnfPassword = false;
                    _cnf_passwordText.setSelection(_passwordText.getText().length());
                    _showCnfPassword.setImageResource(R.drawable.ic_visible_on);
                }
            }

        });
    }

    private void writeSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("hostel_res_id", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("hostel_id", hostelResId);
        editor.apply();
    }

    private void getHostels(String collegeName) {
        switch (collegeName){
            case "IIT Roorkee":
                hostelResId = R.array.iitr_hostel_list;
                break;
            case "IIT Delhi":
                hostelResId = R.array.iitd_hostel_list;
                break;
            case "IIT Bombay":
                hostelResId = R.array.iitb_hostel_list;
                break;
            case "IIT Madras":
                hostelResId = R.array.iitm_hostel_list;
                break;
            default:
                hostelResId = R.array.iitr_hostel_list;
        }
    }

    private void setHostelSpinner(){
        hostelAdapter = ArrayAdapter.createFromResource(this, hostelResId, android.R.layout.simple_spinner_item);
        hostelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _hostelSpinner.setAdapter(hostelAdapter);
        _hostelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hostel = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                hostel = (String) parent.getItemAtPosition(0);
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

        progressDialog = new CustomProgressDialog(SignupActivity.this);
        progressDialog.setCancelable(false);

        String fname = _FnameText.getText().toString();
        String lname = _LnameText.getText().toString();
        String email = _emailText.getText().toString();// + domain;
        String password = _passwordText.getText().toString();
        String room_no = _roomText.getText().toString();
        String roll_no = _rollText.getText().toString();
        String college = _collegeText.getText().toString();
        String contact = _contactText.getText().toString();

        //If the contact no. is empty, sign up directly, else, open the otp dialog to verify the entered contact no.
        if (!contact.equals("")) {
            sendOTP(contact);
        } else {
            requestSignUp(fname, lname, email, password, room_no, roll_no, college, contact);
        }
    }

    private void requestSignUp(final String fname, final String lname, final String email, final String password,
                               final String room_no, final String roll_no, final String college, final String contact) {
        progressDialog.show();
        Helper.setUserEmail(email);
        final UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();

        Call<List<College>> collegeListCall = usersAPI.searchCollege(college);
        collegeListCall.enqueue(new Callback<List<College>>() {
            @Override
            public void onResponse(Call<List<College>> call, Response<List<College>> response) {
                if(response.body() == null || response.body().size() == 0){
                    Call<College> addCollegeCall = usersAPI.addCollege(college, domain);
                    addCollegeCall.enqueue(new Callback<College>() {
                        @Override
                        public void onResponse(Call<College> call, Response<College> response) {
                            if(response.body() != null) {
                                Log.i(TAG, "College Made" + response.toString());
                                createAccount(usersAPI, fname, lname, email, college, room_no, roll_no, contact,
                                        password);
                            }
                            else{
                                Log.i(TAG, "College Not Made"+response.toString());
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<College> call, Throwable t) {
                            Log.i(TAG, "College Not Made"+t.toString());
                            progressDialog.dismiss();
                        }
                    });
                }
                else{
                    createAccount(usersAPI, fname, lname, email, college, room_no, roll_no, contact, password);
                }
            }

            @Override
            public void onFailure(Call<List<College>> call, Throwable t) {
                Log.i(TAG, "College Not Made"+t.toString());
                progressDialog.dismiss();
            }
        });
    }

    public void createAccount(UsersAPI usersAPI, String fname, String lname, String email, String college,
                              String room_no, String roll_no, String contact, String password){

        Call<Signup> userInfoCall = usersAPI.getUserInfo(email, hostel, room_no, roll_no, fname, lname, contact,
                FirebaseInstanceId.getInstance().getToken(),password, college);
        userInfoCall.enqueue(new retrofit2.Callback<Signup>() {
            @Override
            public void onFailure(Call<Signup> call, Throwable t) {
                onSignupFailed("Check your network connection properly");
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Call<Signup> call, Response<Signup> response) {
                if (response.body() != null) {
                    String detail = response.body().getDetail();

                    if (detail.equals("Fill required details or Email id already registered.")) {
                        onSignupFailed("Email already registered" + FirebaseInstanceId.getInstance().getToken());
                        progressDialog.dismiss();
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
        dialog.setCancelable(false);
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
        progressDialog.dismiss();
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed(String toast) {
        String password = _passwordText.getText().toString();
        if (password.length() < 6 || password.length() > 15) {
            Toast.makeText(getBaseContext(), "Password length between 6 and 15 characters", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), toast, Toast.LENGTH_LONG).show();
        }

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

    private void addColleges() {
        colleges.add(new College(getResources().getString(R.string.iitr), "@iitr.ac.in"));
        colleges.add(new College(getResources().getString(R.string.iitd), "@iitd.ac.in"));
        colleges.add(new College(getResources().getString(R.string.iitb), "@iitb.ac.in"));
        colleges.add(new College(getResources().getString(R.string.iitm), "@iitm.ac.in"));
//        colleges.add(new College("IIT Kanpur", "@iitk.ac.in"));
//        colleges.add(new College("IIT Kharagpur", "@iitkgp.ac.in"));
//        colleges.add(new College("IIT Guwahati", "@iitg.ac.in"));
//        colleges.add(new College("IIT Ropar", "@iitrp.ac.in"));
//        colleges.add(new College("IIT Indore", "@iiti.ac.in"));
    }
}
