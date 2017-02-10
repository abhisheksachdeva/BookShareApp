package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ajayrahul on 17/8/16.
 */
public class CustomProgressDialog extends ProgressDialog{
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
        setContentView(R.layout.custom_progressdialog2);


    }
}
