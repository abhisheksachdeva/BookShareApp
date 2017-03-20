package com.sdsmdg.bookshareapp.BSA.api.otp.Models;

public class Response {

    //The response of the get request is the requestId, which can be used to view the delivery report of the message
    String message;
    //if type="success", it means the message was successfully sent
    String type;

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }
}
