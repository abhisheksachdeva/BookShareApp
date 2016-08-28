package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestFactory;
    import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.gson.JsonObject;
import com.sdsmdg.bookshareapp.BSA.GRLogin.AccessToken;
import com.sdsmdg.bookshareapp.BSA.GRLogin.GRLoginInterface;
import com.sdsmdg.bookshareapp.BSA.GRLogin.GoodreadsOAuthResponse;
import com.sdsmdg.bookshareapp.BSA.GRLogin.ServiceGenerator;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.models.GoodreadsResponse;
import com.sdsmdg.bookshareapp.BSA.utils.CommonUtilities;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class GRLogin4 extends AppCompatActivity {

    Button login;
    public static final String BASE_GOODREADS_URL = "https://www.goodreads.com";
    public static final String TOKEN_SERVER_URL = BASE_GOODREADS_URL + "/oauth/request_token";
    public static final String AUTHENTICATE_URL = BASE_GOODREADS_URL + "/oauth/authorize";
    public static final String ACCESS_TOKEN_URL = BASE_GOODREADS_URL + "/oauth/access_token";

    public static final String GOODREADS_KEY = CommonUtilities.API_KEY;
    public static final String GOODREADS_SECRET = CommonUtilities.SECRET;
    public static String authUrl;


    OAuthHmacSigner signer;
    OAuthGetTemporaryToken getTemporaryToken;
    OAuthCredentialsResponse temporaryTokenResponse;
    OAuthAuthorizeTemporaryTokenUrl accessTempToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grlogin);
        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signer = new OAuthHmacSigner();
                // Get Temporary Token
                 getTemporaryToken = new OAuthGetTemporaryToken(TOKEN_SERVER_URL);
                signer.clientSharedSecret = GOODREADS_SECRET;
                getTemporaryToken.signer = signer;
                getTemporaryToken.consumerKey = GOODREADS_KEY;
                getTemporaryToken.transport = new NetHttpTransport();
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            temporaryTokenResponse = getTemporaryToken.execute();
                            accessTempToken = new OAuthAuthorizeTemporaryTokenUrl(AUTHENTICATE_URL);
                            accessTempToken.temporaryToken = temporaryTokenResponse.token;
                            authUrl = accessTempToken.build(); System.out.println("Goodreads oAuth sample: Please visit the following URL to authorize:");
                            System.out.println(authUrl);
                            System.out.println("Waiting 10s to allow time for visiting auth URL and authorizing...");
                            Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(authUrl));
                            startActivity(i);

                            Thread.sleep(20000);
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
                            System.out.println(accessTokenResponse.token+" and "+accessTokenResponse.tokenSecret);
                            Helper.setAccessToken(accessTokenResponse.token);
                            Helper.setAccessSecret(accessTokenResponse.tokenSecret);

                            HttpRequestFactory requestFactory = new ApacheHttpTransport().createRequestFactory(oauthParameters);
                            GenericUrl genericUrl = new GenericUrl("https://www.goodreads.com/api/auth_user");
                            HttpResponse resp = requestFactory.buildGetRequest(genericUrl).execute();
//                            System.out.println(resp.parseAsString());


//                            GoodreadsOAuthResponse g ;
//                            g= resp.parseAs(GoodreadsOAuthResponse.class);
//                            System.out.println(g.getId());


//                            String xml= resp.parseAsString();
//                            System.out.println(resp.parseAsString());
//
//                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                            DocumentBuilder builder;
//                            InputSource is;
//                            try {
//                                builder = factory.newDocumentBuilder();
//                                is = new InputSource(new StringReader(resp.parseAsString()));
//                                Document doc = builder.parse(is);
//                                 String userid = doc.getAttributes().getNamedItem("id").getNodeValue();
//                                System.out.println(userid+"sddg");
//                            } catch (ParserConfigurationException e) {
//                            } catch (SAXException e) {
//                            } catch (IOException e) {
//                            }



                            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
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
                                    Helper.setUserGRid(key);
                                }

                            }catch (ParserConfigurationException p){

                            }catch (SAXException s){

                            }catch (XPathExpressionException x){

                            }


//                            HttpRequestFactory requestFactory2 = new ApacheHttpTransport().createRequestFactory(oauthParameters);
//                            GenericUrl genericUrl2 = new GenericUrl("https://www.goodreads.com/review/list/"+Helper.getUserGRid()
//                                    +".xml?shelf=to-read?key="+CommonUtilities.API_KEY);
//                            HttpResponse resp2 = requestFactory2.buildGetRequest(genericUrl2).execute();
//                            System.out.println(resp2.parseAsString());




                        } catch (IOException e) {
                            e.printStackTrace();

                        }catch (InterruptedException ie){
                            ie.printStackTrace();

                        }
                    }
                };
                thread.start();

                // Build Authenticate URL


                // Redirect to Authenticate URL in order to get Verifier Code


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();



    }

}