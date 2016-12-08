package com.sdsmdg.bookshareapp.BSA.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sdsmdg.bookshareapp.BSA.R;

public class fcm_test extends AppCompatActivity {


    Button token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm_test);
        token = (Button)findViewById(R.id.token);
        token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tok = FirebaseInstanceId.getInstance().getToken();
                Toast.makeText(fcm_test.this,tok, Toast.LENGTH_LONG).show();


            }
        });

    }
}
