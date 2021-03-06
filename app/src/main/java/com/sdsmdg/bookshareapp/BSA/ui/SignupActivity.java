package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
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
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.CollegeAdapter;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = AppCompatActivity.class.getSimpleName();

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
    @InjectView(R.id.fname_input_layout)
    TextInputLayout fnameInputLayout;
    @InjectView(R.id.lname_input_layout)
    TextInputLayout lnameInputLayout;
    @InjectView(R.id.email_input_layout)
    TextInputLayout emailInputLayout;
    @InjectView(R.id.password_input_layout)
    TextInputLayout passwordInputLayout;
    @InjectView(R.id.confirm_password_input_layout)
    TextInputLayout confirmPasswordInputLayout;
    @InjectView(R.id.enroll_input_layout)
    TextInputLayout enrollInputLayout;
    ArrayAdapter<CharSequence> hostelAdapter;
    String domain = "@iitr.ac.in";
    ArrayList<College> colleges;
    int hostelResId = R.array.iitr_hostel_list;
    boolean showPassword = false, showCnfPassword = false;
    CustomProgressDialog progressDialog;
    College college;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        colleges = new ArrayList<>();
        addColleges();
        CollegeAdapter collegeAdapter = new CollegeAdapter(getApplicationContext(), colleges);
        _domainSpinner.setAdapter(collegeAdapter);

        _domainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                college = (College) parent.getItemAtPosition(position);
                domain = college.getCollegeDomain();
                domainTextView.setText(college.getCollegeDomain());
                getHostels(college.getCollegeName());
                setHostelSpinner();
                writeSharedPreferences();
                hostelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                domain = "@iitr.ac.in";
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
                    _showPassword.setImageResource(R.drawable.ic_visible_on);
                } else {
                    _passwordText.setInputType(129); //input type = password
                    showPassword = false;
                    _passwordText.setSelection(_passwordText.getText().toString().length());
                    _showPassword.setImageResource(R.drawable.ic_visible_off);
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
                    _showCnfPassword.setImageResource(R.drawable.ic_visible_on);
                } else {
                    _cnf_passwordText.setInputType(129); //input type = password
                    showCnfPassword = false;
                    _cnf_passwordText.setSelection(_passwordText.getText().length());
                    _showCnfPassword.setImageResource(R.drawable.ic_visible_off);
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
        switch (collegeName) {
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

    private void setHostelSpinner() {
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

    public void signup(String hostel) {

        if (!validate()) {
            onSignupFailed("Fill required details properly.");
            return;
        }

        progressDialog = new CustomProgressDialog(SignupActivity.this);
        progressDialog.setCancelable(false);

        String fname = _FnameText.getText().toString();
        String lname = _LnameText.getText().toString();
        String email = _emailText.getText().toString() + domain;
        String password = _passwordText.getText().toString();
        String room_no = _roomText.getText().toString();
        String roll_no = _rollText.getText().toString();
        String contact = _contactText.getText().toString();

        requestSignUp(fname, lname, email, password, room_no, roll_no, college.getCollegeName(), contact);
    }

    private void requestSignUp(final String fname, final String lname, final String email, final String password,
                               final String room_no, final String roll_no, final String college, final String contact) {
        progressDialog.show();
        Helper.setUserEmail(email);
        final UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();

        Call<List<College>> collegeListCall = usersAPI.searchCollege(college);
        collegeListCall.enqueue(new Callback<List<College>>() {
            @Override
            public void onResponse(@NonNull Call<List<College>> call, @NonNull Response<List<College>> response) {
                if (response.body() == null || response.body().size() == 0) {
                    Call<College> addCollegeCall = usersAPI.addCollege(college, domain);
                    addCollegeCall.enqueue(new Callback<College>() {
                        @Override
                        public void onResponse(@NonNull Call<College> call, @NonNull Response<College> response) {
                            if (response.body() != null) {
                                Log.i(TAG, "College Made" + response.toString());
                                createAccount(usersAPI, fname, lname, email, college, room_no, roll_no, contact,
                                        password);
                            } else {
                                Log.i(TAG, "College Not Made" + response.toString());
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<College> call, @NonNull Throwable t) {
                            Log.i(TAG, "College Not Made" + t.toString());
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    createAccount(usersAPI, fname, lname, email, college, room_no, roll_no, contact, password);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<College>> call, @NonNull Throwable t) {
                Log.i(TAG, "College Not Made" + t.toString());
                progressDialog.dismiss();
            }
        });
    }

    public void createAccount(UsersAPI usersAPI, String fname, String lname, String email, String college,
                              String room_no, String roll_no, String contact, String password) {

        Call<Signup> userInfoCall = usersAPI.getUserInfo(email, hostel, room_no, roll_no, fname, lname, contact,
                FirebaseInstanceId.getInstance().getToken(), password, college);
        userInfoCall.enqueue(new retrofit2.Callback<Signup>() {
            @Override
            public void onFailure(@NonNull Call<Signup> call, @NonNull Throwable t) {
                onSignupFailed("Check your network connection properly");
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(@NonNull Call<Signup> call, @NonNull Response<Signup> response) {
                if (response.body() != null) {
                    String detail = response.body().getDetail();

                    if (detail.equals("Successfully registered.")) {
                        onSignupSuccess();
                    } else {
                        onSignupFailed(detail);
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        progressDialog.dismiss();
        if (_contactText.getText().toString().equals("")) {
            showAlertDialog();
        } else{
            Intent verifyOtpIntent = new Intent(SignupActivity.this, VerifyOtpActivity.class);
            verifyOtpIntent.putExtra("email", _emailText.getText().toString() + domain);
            startActivity(verifyOtpIntent);
            finish();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success!!");
        builder.setMessage("An activation link has been sent to your email. Click it to activate your citadel account.");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void onSignupFailed(String toast) {
        Toast.makeText(getBaseContext(), toast, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String fname = _FnameText.getText().toString();
        String lname = _LnameText.getText().toString();
        String email = _emailText.getText().toString() + domain;
        String password = _passwordText.getText().toString();
        String cnf_password = _cnf_passwordText.getText().toString();
        String roll_no = _rollText.getText().toString();

        if (fname.isEmpty()) {
            fnameInputLayout.setErrorEnabled(true);
            fnameInputLayout.setError("Please fill first name");
            valid = false;
        } else {
            fnameInputLayout.setErrorEnabled(false);
        }
        if (lname.isEmpty()) {
            lnameInputLayout.setErrorEnabled(true);
            lnameInputLayout.setError("Please fill last lame");
            valid = false;
        } else {
            lnameInputLayout.setErrorEnabled(false);
        }
        if (email.isEmpty()) {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError("Enter a valid email address");
            valid = false;
        } else {
            emailInputLayout.setErrorEnabled(false);
        }
        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError("Password must be at least 6 characters in length");
            valid = false;
        } else {
            passwordInputLayout.setErrorEnabled(false);
        }
        if (cnf_password.isEmpty() || !(cnf_password.equals(password))) {
            if (cnf_password.isEmpty()) {
                confirmPasswordInputLayout.setErrorEnabled(true);
                confirmPasswordInputLayout.setError("Please re-enter password");
            } else {
                confirmPasswordInputLayout.setErrorEnabled(true);
                confirmPasswordInputLayout.setError("The passwords do not match.");
            }
            valid = false;
        } else {
            confirmPasswordInputLayout.setErrorEnabled(false);
        }
        if (roll_no.isEmpty()) {
            enrollInputLayout.setErrorEnabled(true);
            enrollInputLayout.setError("Please fill enollment no");
            valid = false;

        } else {
            enrollInputLayout.setErrorEnabled(false);
        }
        return valid;
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
