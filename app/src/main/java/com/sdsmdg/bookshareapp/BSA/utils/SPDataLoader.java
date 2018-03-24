package com.sdsmdg.bookshareapp.BSA.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPDataLoader {

    private SharedPreferences preferences;

    public String getUserName(Context context) {
        preferences = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        return preferences.getString("first_name", null) + " " + preferences.getString("last_name", null);
    }

    public String getUserEmail(Context context) {
        preferences = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        return preferences.getString("email", null);
    }

    public String getHostel(Context context) {
        preferences = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        return preferences.getString("hostel", null);
    }

    public String getRoomNo(Context context) {
        preferences = context.getSharedPreferences("Token", Context.MODE_PRIVATE);
        return preferences.getString("room_no", null);
    }
}
