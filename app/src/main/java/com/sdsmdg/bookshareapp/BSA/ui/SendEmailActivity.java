package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.ui.fragments.SendEmailFragment;

public class SendEmailActivity extends AppCompatActivity implements SendEmailFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        getIntentType((String) getIntent().getExtras().get("email_type"));
    }

    private void getIntentType(String emailType) {
        switch (emailType) {
            case "forgot_password_email":
                initFragment(getResources().getString(R.string.forgot_password),
                        getResources().getString(R.string.forgot_password_message),
                        emailType);
                break;
            case "new_activation_email":
                initFragment(getResources().getString(R.string.new_activation),
                        getResources().getString(R.string.new_activation_message),
                        emailType);
                break;
            case "new_otp":
                initFragment(getResources().getString(R.string.new_otp),
                        getResources().getString(R.string.new_otp_message),
                        emailType);
                break;
        }
    }

    private void initFragment(String title, String description, String emailType) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SendEmailFragment sendEmailFragment = SendEmailFragment.newInstance(title, description, emailType);
        fragmentTransaction.add(R.id.send_email_fragment, sendEmailFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(String status, String email) {
        if (email != null){
            Intent verifyOtpIntent = new Intent(this, VerifyOtpActivity.class);
            verifyOtpIntent.putExtra("email", email);
            startActivity(verifyOtpIntent);
            finish();
        }else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        }
    }
}
