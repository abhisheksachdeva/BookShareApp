package com.sdsmdg.bookshareapp.BSA.api.models;

import com.google.gson.annotations.SerializedName;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;

public class Login {

    @SerializedName("token")
    String token;

    @SerializedName("userInfo")
    UserInfo userInfo;

    @SerializedName("detail")
    String detail;

    public String getToken() {
        return token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public String getDetail() {
        return detail;
    }

}
