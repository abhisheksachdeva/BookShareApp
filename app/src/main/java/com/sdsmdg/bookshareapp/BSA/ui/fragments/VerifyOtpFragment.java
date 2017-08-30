package com.sdsmdg.bookshareapp.BSA.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;

public class VerifyOtpFragment extends DialogFragment {

    Activity activity;
    String generatedOTP;

    public interface OnOTPVerifyListener {
        public void onOTPVerified();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        generatedOTP = getArguments().getString("generated_otp");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Inflating the contents of the layout in a view
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.fragment_verify_otp, null);

        final EditText editText = (EditText) content.findViewById(R.id.edit_text_otp);

        builder.setView(content);
        builder.setTitle("Enter OTP");
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setNegativeButton("Start Over", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getDialog().dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals(generatedOTP)) {
                    Toast.makeText(activity, "Otp verified", Toast.LENGTH_SHORT).show();
                    ((OnOTPVerifyListener) activity).onOTPVerified();
                    dialog.dismiss();
                } else {
                    Toast.makeText(activity, "Incorrect Otp, Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return builder.create();
    }

}
