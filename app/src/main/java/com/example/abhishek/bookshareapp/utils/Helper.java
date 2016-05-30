package com.example.abhishek.bookshareapp.utils;

/**
 * Created by ajayrahul on 30/5/16.
 */
public class Helper {

    public static String userEmail ="default";

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        Helper.userEmail = userEmail;
    }
}
