package com.sdsmdg.bookshareapp.BSA.api.models;

import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;

public class Login {

    String token;

    UserInfo userInfo;

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
