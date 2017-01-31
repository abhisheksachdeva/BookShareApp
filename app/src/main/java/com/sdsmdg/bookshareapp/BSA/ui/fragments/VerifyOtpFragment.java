package com.sdsmdg.bookshareapp.BSA.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sdsmdg.bookshareapp.BSA.R;

public class VerifyOtpFragment extends DialogFragment {

    VerifyOtp verifyOtpInstance;

    public interface VerifyOtp {
        public void verify(String receivedOTP);
    }

    //This method should be called every time an object of this class is created
    public VerifyOtpFragment setVerifyOtpInstance(VerifyOtp verifyOtpInstance) {
        this.verifyOtpInstance = verifyOtpInstance;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Inflating the contents of the layout in a view
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.fragment_verify_otp, null);

        final EditText editText = (EditText)content.findViewById(R.id.edit_text_otp);

        builder.setView(content);
        builder.setTitle("Enter OTP");
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                verifyOtpInstance.verify(editText.getText().toString());
            }
        });
        builder.setNegativeButton("Start Over", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        return builder.create();
    }

}
