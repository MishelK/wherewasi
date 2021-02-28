package com.kdkvit.wherewasi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import models.User;

public class SharedPreferencesUtils {

    public static User getUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String userString = sp.getString("user", null);
        Gson gson = new Gson();
        User user = null;
        if(userString!=null){
            user = (User) gson.fromJson(userString,User.class);
            Log.i("user", String.valueOf(user.getId()));
        }
        return user;
    }

    public static void setUser(Context context,User user) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        sp.edit().putString("user", gson.toJson(user)).apply();
    }
}
