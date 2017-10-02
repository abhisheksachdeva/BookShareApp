package com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks;

import com.google.gson.annotations.SerializedName;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;

import java.util.List;

public class BookDetailWithCancel {


    @SerializedName("book")
    Book book;
    @SerializedName("userInfoList")
    List<UserInfo> userInfoList;
    @SerializedName("cancels")
    List<Boolean> cancels;

    public List<Boolean> getCancels() {
        return cancels;
    }

    int count;

    public int getCount() {
        return count;
    }

    public Book getBook() {
        return book;
    }

    public List<UserInfo> getUserInfoList(){
        return userInfoList;
    }
}
