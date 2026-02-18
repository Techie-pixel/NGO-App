package com.example.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class adminpanel extends AppCompatActivity {

    LinearLayout alluser, adminmessage, admindonation, adminEvents, adminGallery;
    Button adminBackBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adminpanel);

        alluser = findViewById(R.id.allusers);
        adminmessage = findViewById(R.id.adminmessage);
        admindonation = findViewById(R.id.admindonation);
        adminEvents = findViewById(R.id.adminEvents);
        adminGallery = findViewById(R.id.adminGallery);
        adminBackBtn = findViewById(R.id.adminBackBtn);

        alluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(adminpanel.this, R.anim.button_press));
                Intent intent = new Intent(adminpanel.this, alluserlist.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        adminmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(adminpanel.this, R.anim.button_press));
                Intent intent = new Intent(adminpanel.this, adminmsg.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        admindonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(adminpanel.this, R.anim.button_press));
                Intent intent = new Intent(adminpanel.this, admin_donation_activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        adminEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(adminpanel.this, R.anim.button_press));
                Intent intent = new Intent(adminpanel.this, AdminEventsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        adminGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(adminpanel.this, R.anim.button_press));
                Intent intent = new Intent(adminpanel.this, AdminGalleryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        adminBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(adminpanel.this, R.anim.button_press));

                // Clear admin session
                SharedPreferences prefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
                prefs.edit().putBoolean("isAdminLoggedIn", false).apply();

                Intent intent = new Intent(adminpanel.this, login.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}