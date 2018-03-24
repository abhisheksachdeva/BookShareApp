package com.sdsmdg.bookshareapp.BSA.api.models;

import com.google.gson.annotations.SerializedName;

public class Signup {

    @SerializedName("detail")
    private String detail;

    public String getDetail() {
        return detail;
    }
}
