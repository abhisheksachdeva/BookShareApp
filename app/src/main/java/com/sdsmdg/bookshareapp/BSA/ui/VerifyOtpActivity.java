package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.Service;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sdsmdg.bookshareapp.BSA.R;

public class VerifyOtpActivity extends AppCompatActivity implements View.OnFocusChangeListener, TextWatcher, View.OnKeyListener {

    private TextInputEditText pinFirstDigitEditText;
    private TextInputEditText pinSecondDigitEditText;
    private TextInputEditText pinThirdDigitEditText;
    private TextInputEditText pinFourthDigitEditText;
    private TextInputEditText pinFifthDigitEditText;
    private TextInputEditText pinSixthDigitEditText;
    private TextInputEditText pinHiddenEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        initViews();
        setPinListeners();
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

        pinFirstDigitEditText.setOnKeyListener(this);
        pinSecondDigitEditText.setOnKeyListener(this);
        pinThirdDigitEditText.setOnKeyListener(this);
        pinFourthDigitEditText.setOnKeyListener(this);
        pinFifthDigitEditText.setOnKeyListener(this);
        pinSixthDigitEditText.setOnKeyListener(this);
        pinHiddenEditText.setOnKeyListener(this);
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
        editText.requestFocus();
    }

    /**
     * Sets default PIN background.
     *
     * @param editText edit text to change
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setDefaultPinBackground(EditText editText) {
        editText.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorAccent));
    }

    /**
     * Sets focused PIN field background.
     *
     * @param editText edit text to change
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setFocusedPinBackground(EditText editText) {
        editText.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.Red));
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
        } else if (s.length() == 1) {
            setFocusedPinBackground(pinSecondDigitEditText);
            pinFirstDigitEditText.setText(s.charAt(0) + "");
            pinSecondDigitEditText.setText("");
            pinThirdDigitEditText.setText("");
            pinFourthDigitEditText.setText("");
            pinFifthDigitEditText.setText("");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(pinThirdDigitEditText);
            pinSecondDigitEditText.setText(s.charAt(1) + "");
            pinThirdDigitEditText.setText("");
            pinFourthDigitEditText.setText("");
            pinFifthDigitEditText.setText("");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(pinFourthDigitEditText);
            pinThirdDigitEditText.setText(s.charAt(2) + "");
            pinFourthDigitEditText.setText("");
            pinFifthDigitEditText.setText("");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 4) {
            setFocusedPinBackground(pinFifthDigitEditText);
            pinFourthDigitEditText.setText(s.charAt(3) + "");
            pinFifthDigitEditText.setText("");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 5) {
            setFocusedPinBackground(pinSixthDigitEditText);
            pinFifthDigitEditText.setText(s.charAt(4) + "");
            pinSixthDigitEditText.setText("");
        } else if (s.length() == 6) {
            pinSixthDigitEditText.setText(s.charAt(5) + "");
            hideSoftKeyboard(pinSixthDigitEditText);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = view.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (pinHiddenEditText.getText().length() == 6)
                            pinSixthDigitEditText.setText("");
                        else if (pinHiddenEditText.getText().length() == 5)
                            pinFifthDigitEditText.setText("");
                        else if (pinHiddenEditText.getText().length() == 4)
                            pinFourthDigitEditText.setText("");
                        else if (pinHiddenEditText.getText().length() == 3)
                            pinThirdDigitEditText.setText("");
                        else if (pinHiddenEditText.getText().length() == 2)
                            pinSecondDigitEditText.setText("");
                        else if (pinHiddenEditText.getText().length() == 1)
                            pinFirstDigitEditText.setText("");

                        if (pinHiddenEditText.length() > 0)
                            pinHiddenEditText.setText(pinHiddenEditText.getText().subSequence(0, pinHiddenEditText.length() - 1));

                        return true;
                    }
                    break;
                default:
                    return false;
            }
        }
        return false;
    }
}
