package com.sdsmdg.bookshareapp.BSA.utils;

public class Helper {

    public static String accessToken = "";
    public static String accessSecret = "";
    public static String userGRid = null;

    public static String token, id;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Helper.token = token;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Helper.id = id;
    }

    public static String getUserGRid() {
        return userGRid;
    }

    public static void setUserGRid(String userGRid) {
        Helper.userGRid = userGRid;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        Helper.accessToken = accessToken;
    }

    public static String getAccessSecret() {
        return accessSecret;
    }

    public static void setAccessSecret(String accessSecret) {
        Helper.accessSecret = accessSecret;
    }

    public static String userEmail = "";
    public static String userName = "";
    public static String userId = "";
    public static String bookId = "";
    public static Integer new_total = 0;
    public static Integer old_total = 1;
    public static boolean imageChanged = false;

    public static Integer getNew_total() {
        return new_total;
    }

    public static void setNew_total(Integer new_total) {
        Helper.new_total = new_total;
    }

    public static Integer getOld_total() {
        return old_total;
    }

    public static void setOld_total(Integer old_total) {
        Helper.old_total = old_total;
    }

    public static String getBookTitle() {
        return bookTitle;
    }

    public static void setBookTitle(String bookTitle) {
        Helper.bookTitle = bookTitle;
    }

    public static String getBookId() {
        return bookId;
    }

    public static void setBookId(String bookId) {
        Helper.bookId = bookId;
    }

    public static String bookTitle = "";

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        Helper.userId = userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        Helper.userName = userName;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        Helper.userEmail = userEmail;
    }
}
