package com.sdsmdg.bookshareapp.BSA.api.otp;

import com.sdsmdg.bookshareapp.BSA.api.otp.Models.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MSGApi {

    @GET("api/sendhttp.php")
    Call<Response> sendOTP(
            @Query("authkey") String authKey,
            @Query("mobiles") String phoneNo,
            @Query("message") String message,
            @Query("sender") String senderID,//The name of sender(6 characters for india), e.g. CITADL
            @Query("route") int route,//The route is 4 for transactional sms
            @Query("country") int country,//country is 91 for india, 0 for international, 1 for USA
            @Query("response") String responseType//can be json, or xml
    );

}
