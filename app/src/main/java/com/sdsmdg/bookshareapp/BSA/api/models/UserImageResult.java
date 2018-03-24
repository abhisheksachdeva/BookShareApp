package com.sdsmdg.bookshareapp.BSA.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by harshit on 30/10/17.
 */

public class UserImageResult {

    @SerializedName("detail")
    private String detail;

    @SerializedName("image_url")
    private String imageUrl;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
