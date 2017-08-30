package com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks;

import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;

import java.util.List;

public class BookDetailWithCancel {


    Book book;
    List<UserInfo> userInfoList;
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
