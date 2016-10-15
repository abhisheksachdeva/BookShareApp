package com.sdsmdg.bookshareapp.BSA.api.models.Notification;
import android.app.Notification;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ajayrahul on 9/6/16.
 */
public class Notification_Model {

    @SerializedName("count")
    int count;

    @SerializedName("next")
    int next;

    @SerializedName("previous")
    int previous;

    @SerializedName("results")
    List<Notifications> notificationsList;
    public int getPrevious() {
        return previous;
    }

    public int getNext() {
        return next;
    }

    public List<Notifications> getNotificationsList() {
        return notificationsList;
    }

    public int getCount() {
        return count;
    }
}
