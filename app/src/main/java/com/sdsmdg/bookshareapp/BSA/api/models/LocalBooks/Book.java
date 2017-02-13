package com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks;

import com.google.gson.annotations.SerializedName;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalUsers.UserInfo;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Book extends RealmObject {

    String id;
    String detail;
    String title;
    String author;
    float rating;
    @SerializedName("ratings_count")
    Long ratingsCount;
    @SerializedName("gr_id")
    String grId;
    @SerializedName("gr_img_url")
    String grImgUrl;
    @SerializedName("email")
    String email;
    @SerializedName("owner")
    RealmList<UserInfo> userInfoList;
    @SerializedName("description")
    String description;

    public String getAuthor() {
        return author;
    }

    public String getEmail() {
        return email;
    }

    public String getGrId() {
        return grId;
    }

    public String getGrImgUrl() {
        return grImgUrl;
    }

    public String getId() {
        return id;
    }

    public String getDetail() {
        return detail;
    }

    public float getRating() {
        return rating;
    }

    public Long getRatingsCount() {
        return ratingsCount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }
}
