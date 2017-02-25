package com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserDetailWithCancel {

    @SerializedName("user_info")
    UserInfo userInfo;

    List<Boolean> cancels;

    public List<Boolean> getCancels() {
        return cancels;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
