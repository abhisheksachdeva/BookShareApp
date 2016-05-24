package com.example.abhishek.bookshareapp.api.models;

import com.google.gson.annotations.SerializedName;

public class Login {

    @SerializedName("detail")
    String token;

    public String getToken() {
        return token;
    }
}
