package com.example.abhishek.bookshareapp.api.models.VerifyToken;

import com.google.gson.annotations.SerializedName;

public class UserEmail {

    @SerializedName("detail")
    String email;

    public String getEmail() {
        return email;
    }
}
