package com.example.abhishek.bookshareapp.utils;

public class Helper {

    public static String userEmail = "";
    public static String userName = "";
    public static String userId = "";
    public static String bookId = "";

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
