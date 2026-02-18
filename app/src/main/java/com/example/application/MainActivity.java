package com.example.application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // No splash screen — directly navigate to the right screen
        SharedPreferences prefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
        boolean isAdminLoggedIn = prefs.getBoolean("isAdminLoggedIn", false);

        Intent intent;
        if (isAdminLoggedIn) {
            intent = new Intent(this, adminpanel.class);
        } else {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                intent = new Intent(this, homepage.class);
            } else {
                intent = new Intent(this, login.class);
            }
        }

        startActivity(intent);
        finish();
    }
}