package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sdsmdg.bookshareapp.BSA.GRLogin.AccessToken;
import com.sdsmdg.bookshareapp.BSA.GRLogin.GRLoginInterface;
import com.sdsmdg.bookshareapp.BSA.GRLogin.GoodreadsOAuthSample;
import com.sdsmdg.bookshareapp.BSA.GRLogin.ServiceGenerator;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;

import java.io.IOException;

import retrofit2.Call;

public class GRLogin2 extends AppCompatActivity {

    Button login;
    private final String key = CommonUtilities.API_KEY;
    private final String clientSecret = CommonUtilities.SECRET;
    private final String redirectUri = CommonUtilities.goodreads_api_url+"review/list/?shelf=to-read";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grlogin);
        login = (Button)findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse(ServiceGenerator.API_BASE_URL + "oauth/authorize?mobile=1" + "?key=" + key + "?secret=" + clientSecret+"&redirect_uri=" + redirectUri));
//                startActivity(intent);

                Intent i = new Intent(
                            Intent.ACTION_VIEW,
                        Uri.parse(GoodreadsOAuthSample.getAuthUrl()));
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("Accessss : ","ff");
//        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
//        Uri uri = getIntent().getData();
//        if (uri != null && uri.toString().startsWith(redirectUri)) {
//            // use the parameter your API exposes for the code (mostly it's "code")
//            String code = uri.getQueryParameter("code");
//            if (code != null) {
//                // get access token
//                try {
//                    GRLoginInterface loginService =
//                            ServiceGenerator.createService(GRLoginInterface.class, key, clientSecret);
//                    Call<AccessToken> call = loginService.getAccessToken(code, "authorization_code");
//                    AccessToken accessToken = call.execute().body();
//                    Log.i("access",accessToken.getAccessToken());
//                }
//                catch (IOException i){
//                    Log.i("IOexc",i.toString());
//                }
//
//            } else if (uri.getQueryParameter("error") != null) {
//                // show an error message here
//            }
//        }
    }

}
