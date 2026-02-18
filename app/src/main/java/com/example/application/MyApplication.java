package com.example.application;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Disk Persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
