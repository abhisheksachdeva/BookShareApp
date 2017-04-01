package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.sdsmdg.bookshareapp.BSA.R;

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
        setContentView(R.layout.custom_progressdialog2);

        //TODO:Fix the lottie animation
//        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
//        animationView.setImageAssetsFolder("images");
//        animationView.setAnimation("PinJump.json", LottieAnimationView.CacheStrategy.Strong);
//        animationView.playAnimation();
//        animationView.loop(true);


    }
}
