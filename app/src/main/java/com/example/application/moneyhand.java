package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class moneyhand extends AppCompatActivity {

    Button donatehere;
    ImageView homing, money, profile456, developer123;
    ImageView[] allTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_moneyhand);

        // Bind UI elements
        donatehere = findViewById(R.id.donatehere);
        homing = findViewById(R.id.homing);
        money = findViewById(R.id.money);
        profile456 = findViewById(R.id.profile456);
        developer123 = findViewById(R.id.developer123);

        // Initialize all tab icons
        allTabs = new ImageView[] { homing, money, profile456, developer123 };
        setActiveTab(money);

        // Set click listeners with animations
        donatehere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(moneyhand.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                Intent intent = new Intent(moneyhand.this, donationbox.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        homing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(moneyhand.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                setActiveTab(homing);
                Intent intent = new Intent(moneyhand.this, homepage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(moneyhand.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                setActiveTab(money);
                Toast.makeText(getApplicationContext(), "Donation", Toast.LENGTH_LONG).show();
            }
        });

        profile456.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(moneyhand.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                setActiveTab(profile456);
                Intent intent = new Intent(moneyhand.this, profilepage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        developer123.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(moneyhand.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                setActiveTab(developer123);
                Intent intent = new Intent(moneyhand.this, developer.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    // Highlights selected tab and resets others
    private void setActiveTab(ImageView selectedTab) {
        for (ImageView tab : allTabs) {
            if (tab != null) {
                tab.setBackgroundResource(R.drawable.inactive_tab_bg);
            }
        }
        if (selectedTab != null) {
            selectedTab.setBackgroundResource(R.drawable.active_tab_bg);
        }
    }
}
