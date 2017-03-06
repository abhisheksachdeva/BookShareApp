package com.sdsmdg.bookshareapp.BSA.firebase_classes;

import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.VerifyToken.Detail;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInsIDService";
    String token = Helper.getToken();
    String id = Helper.getId();


    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.d(TAG, "New Token : " + refreshedToken);
        //save token in ur server.

        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Detail> call = usersAPI.update_fcm_id(
                token,
                refreshedToken
        );
        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                if (response.body() != null) {
                    if (response.body().getDetail().equals("FCM_ID changed")) {
                        //Toast.makeText(getApplicationContext(), "FCM_ID changed", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getApplicationContext(), "Request not valid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Log.i("CPA", "request body is null");
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
