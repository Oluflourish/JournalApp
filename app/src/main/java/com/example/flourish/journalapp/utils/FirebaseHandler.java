package com.example.flourish.journalapp.utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Flourish on 01/07/2018.
 */

public class FirebaseHandler extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
