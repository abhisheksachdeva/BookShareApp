package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.sdsmdg.bookshareapp.BSA.R;

/**
 * Created by ajayrahul on 17/8/16.
 */
public class CustomProgressDialog extends ProgressDialog {
    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progressdialog);


    }
}
