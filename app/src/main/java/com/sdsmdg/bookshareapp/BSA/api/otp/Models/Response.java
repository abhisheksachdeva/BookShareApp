package com.sdsmdg.bookshareapp.BSA.api.otp.Models;

import com.google.gson.annotations.SerializedName;

public class Response {

    //The response of the get request is the requestId, which can be used to view the delivery report of the message
    @SerializedName("message")
    String message;
    //if type="success", it means the message was successfully sent
    @SerializedName("type")
    String type;

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }
}
