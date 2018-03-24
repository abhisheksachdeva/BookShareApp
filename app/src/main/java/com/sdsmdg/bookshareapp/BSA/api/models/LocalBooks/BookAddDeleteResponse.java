package com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks;

import com.google.gson.annotations.SerializedName;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;

import java.util.List;

/**
 * Created by harshit on 17/1/18.
 */

public class BookAddDeleteResponse {

    @SerializedName("detail")
    String detail;

    @SerializedName("cancels")
    List<Boolean> cancelList;

    @SerializedName("userInfoList")
    List<UserInfo> userList;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<Boolean> getCancelList() {
        return cancelList;
    }

    public void setCancelList(List<Boolean> cancelList) {
        this.cancelList = cancelList;
    }

    public List<UserInfo> getUserList() {
        return userList;
    }

    public void setUserList(List<UserInfo> userList) {
        this.userList = userList;
    }
}
