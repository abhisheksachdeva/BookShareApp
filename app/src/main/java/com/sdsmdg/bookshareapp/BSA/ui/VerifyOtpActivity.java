package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.Listeners.SmsListener;
import com.sdsmdg.bookshareapp.BSA.Listeners.SmsReceiver;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity implements
        View.OnFocusChangeListener, TextWatcher, SmsListener {

    private static final  int MY_PERMISSIONS_REQUEST_READ_SMS = 100;
    private TextInputEditText pinFirstDigitEditText;
    private TextInputEditText pinSecondDigitEditText;
    private TextInputEditText pinThirdDigitEditText;
    private TextInputEditText pinFourthDigitEditText;
    private TextInputEditText pinFifthDigitEditText;
    private TextInputEditText pinSixthDigitEditText;
    private TextInputEditText pinHiddenEditText;
    private Button submitOtpButton;
    private CustomProgressDialog progressDialog;
    private String email = null;
    private String contact = null;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        initViews();
        setPinListeners();
        checkSmsPermission();
        bindListeners();
        regListeners();
        if (getIntent().getExtras() != null){
            email = (String) getIntent().getExtras().get("email");
            contact = (String) getIntent().getExtras().get("contact");
        }
    }

    /**
     * Register listeners for button click
     */
    private void regListeners() {
        submitOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pinHiddenEditText.getText().length() == 6){
                    submitOtpButton.setEnabled(false);
                    progressDialog = new CustomProgressDialog(VerifyOtpActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    final UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
                    Call<Detail> verifyOtpCall;
                    if (contact == null) {
                         verifyOtpCall = usersAPI.
                                verifyOtp(email, pinHiddenEditText.getText().toString());
                    }else{
                        verifyOtpCall = usersAPI.
                                changeMobile("Token " + preferences.getString("token", null),
                                        email, contact, pinHiddenEditText.getText().toString());
                    }
                    verifyOtpCall.enqueue(new Callback<Detail>() {
                        @Override
                        public void onResponse(@NonNull Call<Detail> call, @NonNull Response<Detail> response) {
                            if (response.body() != null){
                                String detail = response.body().getDetail();
                                if (detail.equals("Congratulations, the mobile has been verified!!")) {
                                    onSignupSuccess();
                                } else {
                                    onSignupFailed("The otp entered is incorrect.");
                                    progressDialog.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Detail> call, @NonNull Throwable t) {
                            onSignupFailed("Check your network connection properly");
                            progressDialog.dismiss();
                        }
                    });
                } else{
                    Toast.makeText(VerifyOtpActivity.this,
                            "The otp must be at least 6 numbers in length", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onSignupSuccess() {
        submitOtpButton.setEnabled(true);
        progressDialog.dismiss();
        showSuccessUI();
    }

    private void showSuccessUI() {
        if (contact == null) {
            showAlertDialog();
        }else {
            Toast.makeText(this, "Congratulations!! the mobile is verified", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("contact_no", contact);
                    editor.apply();
                    finish();

                }
            }, 1000);
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
        submitOtpButton.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "The mobile number is not verified", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (contact == null) {
                    Intent loginIntent = new Intent(VerifyOtpActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(loginIntent);
                    finish();
                }else{
                    finish();
                }
            }
        }, 1000);
    }

    /**
     * Check permission for reading sms
     */
    private void checkSmsPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Read Sms Permission");
                builder.setMessage("Allow Citadel app to read your sms to automatically verify one time password sent.");
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_SMS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    PackageManager pm  = this.getPackageManager();
                    ComponentName componentName = new ComponentName(this, SmsReceiver.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    PackageManager pm  = this.getPackageManager();
                    ComponentName componentName = new ComponentName(this, SmsReceiver.class);
                    pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                }
                break;
        }
    }

    /**
     * Bind to receiver to receive message
     */
    private void bindListeners() {
        SmsReceiver.bindListener(this);
    }

    /**
     * Initialize EditText fields.
     */
    private void initViews() {
        pinFirstDigitEditText = (TextInputEditText) findViewById(R.id.txt_first_pin);
        pinSecondDigitEditText = (TextInputEditText) findViewById(R.id.txt_second_pin);
        pinThirdDigitEditText = (TextInputEditText) findViewById(R.id.txt_third_pin);
        pinFourthDigitEditText = (TextInputEditText) findViewById(R.id.txt_fourth_pin);
        pinFifthDigitEditText = (TextInputEditText) findViewById(R.id.txt_fifth_pin);
        pinSixthDigitEditText = (TextInputEditText) findViewById(R.id.txt_sixth_pin);
        pinHiddenEditText = (TextInputEditText) findViewById(R.id.pin_hidden_edittext);
        submitOtpButton = (Button) findViewById(R.id.submit_otp_button);
        preferences = getSharedPreferences("Token", MODE_PRIVATE);
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPinListeners() {
        pinHiddenEditText.addTextChangedListener(this);

        pinFirstDigitEditText.setOnFocusChangeListener(this);
        pinSecondDigitEditText.setOnFocusChangeListener(this);
        pinThirdDigitEditText.setOnFocusChangeListener(this);
        pinFourthDigitEditText.setOnFocusChangeListener(this);
        pinFifthDigitEditText.setOnFocusChangeListener(this);
        pinSixthDigitEditText.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            if (pinHiddenEditText.getText().length() == 0){
                setFocusedPinBackground(pinFirstDigitEditText);
            }
            if (pinHiddenEditText.getText().length() == 6){
                setFocusedPinBackground(pinSixthDigitEditText);
            }
            setFocus(pinHiddenEditText);
            showSoftKeyboard(pinHiddenEditText);
        }
    }

    /**
     * Hides soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }


    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets user_default_image PIN background.
     *
     * @param editText edit text to change
     */
    private void setDefaultPinBackground(EditText editText) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && editText instanceof AppCompatEditText) {
            editText.setBackgroundTintList
                    (ContextCompat.getColorStateList(this, R.color.colorAccent));
        } else {
            ViewCompat.setBackgroundTintList(editText, ContextCompat.getColorStateList(this, R.color.colorAccent));
        }
    }

    /**
     * Sets focused PIN field background.
     *
     * @param editText edit text to change
     */
    private void setFocusedPinBackground(EditText editText) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && editText instanceof AppCompatEditText) {
            editText.setBackgroundTintList
                    (ContextCompat.getColorStateList(this, R.color.Red));
        } else {
            ViewCompat.setBackgroundTintList(editText, ContextCompat.getColorStateList(this, R.color.Red));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {

        setDefaultPinBackground(pinFirstDigitEditText);
        setDefaultPinBackground(pinSecondDigitEditText);
        setDefaultPinBackground(pinThirdDigitEditText);
        setDefaultPinBackground(pinFourthDigitEditText);
        setDefaultPinBackground(pinFifthDigitEditText);
        setDefaultPinBackground(pinSixthDigitEditText);

        if (s.length() == 0) {
            setFocusedPinBackground(pinFirstDigitEditText);
            pinFirstDigitEditText.setText("");
            pinSecondDigitEditText.setText("");
            pinThirdDigitEditText.setText("");
            pinFourthDigitEditText.setText("");
            pinFifthDigitEditText.setText("");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(pinSecondDigitEditText);
            pinFirstDigitEditText.setText(String.valueOf(s.charAt(0)));
            pinSecondDigitEditText.setText("");
            pinThirdDigitEditText.setText("");
            pinFourthDigitEditText.setText("");
            pinFifthDigitEditText.setText("");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(pinThirdDigitEditText);
            pinSecondDigitEditText.setText(String.valueOf(s.charAt(1)));
            pinThirdDigitEditText.setText("");
            pinFourthDigitEditText.setText("");
            pinFifthDigitEditText.setText("");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(pinFourthDigitEditText);
            pinThirdDigitEditText.setText(String.valueOf(s.charAt(2)));
            pinFourthDigitEditText.setText("");
            pinFifthDigitEditText.setText("");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 4) {
            setFocusedPinBackground(pinFifthDigitEditText);
            pinFourthDigitEditText.setText(String.valueOf(s.charAt(3)));
            pinFifthDigitEditText.setText("");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 5) {
            setFocusedPinBackground(pinSixthDigitEditText);
            pinFifthDigitEditText.setText(String.valueOf(s.charAt(4)));
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 6) {
            pinSixthDigitEditText.setText(String.valueOf(s.charAt(5)));
            hideSoftKeyboard(pinSixthDigitEditText);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void messageReceived(String messageText) {
        String[] words = messageText.split(" ");
        String otp = words[8];
        pinHiddenEditText.setText(otp);
        pinHiddenEditText.setSelection(pinHiddenEditText.getText().length());
        pinFirstDigitEditText.setText(String.valueOf(otp.charAt(0)));
        pinSecondDigitEditText.setText(String.valueOf(otp.charAt(1)));
        pinThirdDigitEditText.setText(String.valueOf(otp.charAt(2)));
        pinFourthDigitEditText.setText(String.valueOf(otp.charAt(3)));
        pinFifthDigitEditText.setText(String.valueOf(otp.charAt(4)));
        pinSixthDigitEditText.setText(String.valueOf(otp.charAt(5)));
    }
}
