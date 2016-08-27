package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.sdsmdg.bookshareapp.BSA.GRLogin.AccessToken;
import com.sdsmdg.bookshareapp.BSA.GRLogin.GRLoginInterface;
import com.sdsmdg.bookshareapp.BSA.GRLogin.ServiceGenerator;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;

import java.io.IOException;
import java.io.InterruptedIOException;

import retrofit2.Call;

public class GRLogin3 extends AppCompatActivity {

    Button login;
    private final String key = CommonUtilities.API_KEY;
    private final String clientSecret = CommonUtilities.SECRET;
    private final String redirectUri = CommonUtilities.goodreads_api_url+"review/list/?shelf=to-read";

    public static final String BASE_GOODREADS_URL = "https://www.goodreads.com";
    public static final String TOKEN_SERVER_URL = BASE_GOODREADS_URL + "/oauth/request_token";
    public static final String AUTHENTICATE_URL = BASE_GOODREADS_URL + "/oauth/authorize";
    public static final String ACCESS_TOKEN_URL = BASE_GOODREADS_URL + "/oauth/access_token";

    public static final String GOODREADS_KEY = CommonUtilities.API_KEY;
    public static final String GOODREADS_SECRET = CommonUtilities.SECRET;
    public static String authUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grlogin);
        login = (Button)findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getTokken();
                }catch (InterruptedIOException io){
                    Log.i("Ddd","D");

                }catch (IOException i ){
                    Log.i("Ddd","D");


                }catch (InterruptedException ii){
                    Log.i("Ddd","D");


                }

                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(authUrl));
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            // use the parameter your API exposes for the code (mostly it's "code")
            String code = uri.getQueryParameter("code");
            if (code != null) {
                // get access token
                try {
                    GRLoginInterface loginService =
                            ServiceGenerator.createService(GRLoginInterface.class, key, clientSecret);
                    Call<AccessToken> call = loginService.getAccessToken(code, "authorization_code");
                    AccessToken accessToken = call.execute().body();
                    Log.i("access",accessToken.getAccessToken());
                }
                catch (IOException i){
                    Log.i("IOexc",i.toString());
                }

            } else if (uri.getQueryParameter("error") != null) {
                // show an error message here
            }
        }
    }

    public static void getTokken() throws IOException, InterruptedException {
        OAuthHmacSigner signer = new OAuthHmacSigner();
        // Get Temporary Token
        OAuthGetTemporaryToken getTemporaryToken = new OAuthGetTemporaryToken(TOKEN_SERVER_URL);
        signer.clientSharedSecret = GOODREADS_SECRET;
        getTemporaryToken.signer = signer;
        getTemporaryToken.consumerKey = GOODREADS_KEY;
        getTemporaryToken.transport = new NetHttpTransport();
        OAuthCredentialsResponse temporaryTokenResponse = getTemporaryToken.execute();

        // Build Authenticate URL
        OAuthAuthorizeTemporaryTokenUrl accessTempToken = new OAuthAuthorizeTemporaryTokenUrl(AUTHENTICATE_URL);
        accessTempToken.temporaryToken = temporaryTokenResponse.token;
        authUrl = accessTempToken.build();

        // Redirect to Authenticate URL in order to get Verifier Code
        System.out.println("Goodreads oAuth sample: Please visit the following URL to authorize:");
        System.out.println(authUrl);
        System.out.println("Waiting 10s to allow time for visiting auth URL and authorizing...");
        Thread.sleep(20000);

        System.out.println("Waiting time complete - assuming access granted and attempting to get access token");
        // Get Access Token using Temporary token and Verifier Code
        OAuthGetAccessToken getAccessToken = new OAuthGetAccessToken(ACCESS_TOKEN_URL);
        getAccessToken.signer = signer;
        // NOTE: This is the main difference from the StackOverflow example
        signer.tokenSharedSecret = temporaryTokenResponse.tokenSecret;
        getAccessToken.temporaryToken = temporaryTokenResponse.token;
        getAccessToken.transport = new NetHttpTransport();
        getAccessToken.consumerKey = GOODREADS_KEY;
        OAuthCredentialsResponse accessTokenResponse = getAccessToken.execute();

        // Build OAuthParameters in order to use them while accessing the resource
        OAuthParameters oauthParameters = new OAuthParameters();
        signer.tokenSharedSecret = accessTokenResponse.tokenSecret;
        oauthParameters.signer = signer;
        oauthParameters.consumerKey = GOODREADS_KEY;
        oauthParameters.token = accessTokenResponse.token;

        // Use OAuthParameters to access the desired Resource URL
        HttpRequestFactory requestFactory = new ApacheHttpTransport().createRequestFactory(oauthParameters);
        GenericUrl genericUrl = new GenericUrl("https://www.goodreads.com/review/list/?shelf=to-read");
        HttpResponse resp = requestFactory.buildGetRequest(genericUrl).execute();
        System.out.println(resp.parseAsString());
    }

}
