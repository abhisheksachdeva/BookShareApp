package com.sdsmdg.bookshareapp.BSA.api.models.Notification;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ajayrahul on 9/6/16.
 */
public class Notification_Model {

    @SerializedName("count")
    String count;

    @SerializedName("next")
    String next;

    @SerializedName("previous")
    String previous;

    @SerializedName("page")
    String page;

    public String getPage() {
        return page;
    }

    @SerializedName("results")
    List<Notifications> notificationsList;

    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }

    public List<Notifications> getNotificationsList() {
        return notificationsList;
    }

    public String getCount() {
        return count;
    }
}
