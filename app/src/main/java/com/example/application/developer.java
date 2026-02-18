package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class developer extends AppCompatActivity {

    ImageView homing, money, profile456, developer123;
    ImageView[] allTabs; // ✅ For active/inactive tab switching

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_developer);

        // Bind UI elements
        homing = findViewById(R.id.homing);
        money = findViewById(R.id.money);
        profile456 = findViewById(R.id.profile456);
        developer123 = findViewById(R.id.developer123);

        // Initialize tab array and set active
        allTabs = new ImageView[] { homing, money, profile456, developer123 };
        setActiveTab(developer123); // ✅ This tab is active by default

        // Click listeners
        homing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(developer.this, R.anim.button_press));
                setActiveTab(homing);
                startActivity(new Intent(developer.this, homepage.class));
            }
        });

        money.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(developer.this, R.anim.button_press));
                setActiveTab(money);
                startActivity(new Intent(developer.this, moneyhand.class));
            }
        });

        profile456.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(developer.this, R.anim.button_press));
                setActiveTab(profile456);
                startActivity(new Intent(developer.this, profilepage.class));
            }
        });

        developer123.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(developer.this, R.anim.button_press));
                setActiveTab(developer123);
                Toast.makeText(getApplicationContext(), "Developer", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // ✅ Highlights the selected tab, resets others
    private void setActiveTab(ImageView selectedTab) {
        for (ImageView tab : allTabs) {
            tab.setBackgroundResource(R.drawable.inactive_tab_bg);
        }
        selectedTab.setBackgroundResource(R.drawable.active_tab_bg);
    }
}
