package com.sdsmdg.bookshareapp.BSA.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

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
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class GRLoginActivity extends AppCompatActivity {

    Button login;
    TextView textView;
    //url tags
    public static final String BASE_GOODREADS_URL = "https://www.goodreads.com";
    public static final String TOKEN_SERVER_URL = BASE_GOODREADS_URL + "/oauth/request_token";
    public static final String AUTHENTICATE_URL = BASE_GOODREADS_URL + "/oauth/authorize?mobile=1";
    public static final String ACCESS_TOKEN_URL = BASE_GOODREADS_URL + "/oauth/access_token";

    public static final String GOODREADS_KEY = CommonUtilities.API_KEY;
    public static final String GOODREADS_SECRET = CommonUtilities.SECRET;
    public static String authUrl;

    OAuthHmacSigner signer;
    OAuthGetTemporaryToken getTemporaryToken;
    OAuthCredentialsResponse temporaryTokenResponse;
    OAuthAuthorizeTemporaryTokenUrl accessTempToken;
    SharedPreferences pref;
    WebView webView;
    boolean success = true;
    Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grlogin);
        login = (Button) findViewById(R.id.gr_login);
        textView = (TextView) findViewById(R.id.info_text);
        pref = getApplicationContext().getSharedPreferences("UserId", MODE_PRIVATE);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(GRLoginActivity.this);
                builder.setMessage("Continue without SSL certificate?");
                builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        /** webview settings */
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.clearCache(true);
        clearCookies(webView.getContext());


        //Here we are generating a temporary token
        signer = new OAuthHmacSigner();
        // Get Temporary Token
        getTemporaryToken = new OAuthGetTemporaryToken(TOKEN_SERVER_URL);
        signer.clientSharedSecret = GOODREADS_SECRET;
        getTemporaryToken.signer = signer;
        getTemporaryToken.consumerKey = GOODREADS_KEY;
        getTemporaryToken.transport = new NetHttpTransport();


        /**Everything is inside this thread,
         the webview is updated using runOnUiThread
         */
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    temporaryTokenResponse = getTemporaryToken.execute();
                    accessTempToken = new OAuthAuthorizeTemporaryTokenUrl(AUTHENTICATE_URL);
                    accessTempToken.temporaryToken = temporaryTokenResponse.token;
                    authUrl = accessTempToken.build();
                    System.out.println("Goodreads oAuth sample: Please visit the following URL to authorize:");

                    /**this is the authentication url*/

                    System.out.println(authUrl);
                    GRLoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //opening it on webview, if its already authenticated..will open homepage..else sign in page of GR
                            webView.loadUrl(authUrl);
                        }
                    });

                    /** we are obtaining the access token ...*/

                    OAuthGetAccessToken getAccessToken = new OAuthGetAccessToken(ACCESS_TOKEN_URL);

                    getAccessToken.signer = signer;

                    signer.tokenSharedSecret = temporaryTokenResponse.tokenSecret;
                    getAccessToken.temporaryToken = temporaryTokenResponse.token;
                    getAccessToken.transport = new NetHttpTransport();
                    getAccessToken.consumerKey = GOODREADS_KEY;
                    if (Thread.interrupted()) {
                        throw new InterruptedException();

                    }

                    OAuthCredentialsResponse accessTokenResponse = null;
                    final Long start = System.currentTimeMillis();
                    Long s;
                    while (true) {
                        if (Thread.interrupted()) {
                            throw new InterruptedException();

                        }

                        s = System.currentTimeMillis() - start;
                        /**if at all timer is required
                         // we've put up a timeout of s seconds..
                         */

                        try {

                            accessTokenResponse = getAccessToken.execute();
                            if (!accessTokenResponse.toString().contains("Invalid OAuth Request")) {
                                break;
                            }
                        } catch (IOException e) {
                        }

                    }


                    if (accessTokenResponse == null) {
                        success = false;
                        throw new IOException("nill");

                    }

                    OAuthParameters oauthParameters = new OAuthParameters();
                    signer.tokenSharedSecret = accessTokenResponse.tokenSecret;

                    oauthParameters.signer = signer;
                    oauthParameters.consumerKey = GOODREADS_KEY;
                    oauthParameters.token = accessTokenResponse.token;
                    System.out.println(accessTokenResponse.token + " and " + accessTokenResponse.tokenSecret);
                    Helper.setAccessToken(accessTokenResponse.token);
                    Helper.setAccessSecret(accessTokenResponse.tokenSecret);

                    HttpRequestFactory requestFactory = new ApacheHttpTransport().createRequestFactory(oauthParameters);
                    GenericUrl genericUrl = new GenericUrl("https://www.goodreads.com/api/auth_user");
                    HttpResponse resp = requestFactory.buildGetRequest(genericUrl).execute();

                    //for extracting the id from the xml http response..

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    InputSource is;
                    try {
                        builder = factory.newDocumentBuilder();
                        is = new InputSource(new StringReader(resp.parseAsString()));
                        Document doc = builder.parse(is);
                        XPathFactory xPathfactory = XPathFactory.newInstance();
                        XPath xpath = xPathfactory.newXPath();
                        XPathExpression expr = xpath.compile("//GoodreadsResponse/user[@id]");
                        NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

                        for (int x = 0; x < nl.getLength(); x++) {
                            Node currentItem = nl.item(x);
                            String key = currentItem.getAttributes().getNamedItem("id").getNodeValue();
                            System.out.println(key);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("userGrId", key);
                            editor.apply();
                            Helper.setUserGRid(key);
                        }

                    } catch (ParserConfigurationException p) {

                    } catch (SAXException sa) {

                    } catch (XPathExpressionException x) {

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    if (e.toString() == "nill") {
                        success = false;
                        Intent i = new Intent(GRLoginActivity.this, GRLoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch (InterruptedException i) {
                    Intent inn = new Intent(GRLoginActivity.this, MainActivity.class);
                    startActivity(inn);
                    finish();

                } finally {
                    SharedPreferences preff = getSharedPreferences("UserId", MODE_PRIVATE);

                    if (success && preff.getString("userGrId", null) != null) {
                        Intent intent = new Intent(GRLoginActivity.this, ToReadActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (preff.getString("userGrId", null) == null) {
                        Intent intent = new Intent(GRLoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }


            }


        };
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                thread.start();

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onBackPressed() {
        thread.interrupt();
        finish();
        super.onBackPressed();

    }


    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("Cookie", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d("Cookie", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}