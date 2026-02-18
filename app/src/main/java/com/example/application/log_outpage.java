package com.example.application;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class log_outpage extends AppCompatActivity {

    Button logoutbtn;

    ImageView homing, money, profile456, developer123;
    ImageView[] allTabs;

    FirebaseAuth mauth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_outpage);

        // Firebase Auth instance
        mauth = FirebaseAuth.getInstance();

        // Button & ImageView bindings
        logoutbtn = findViewById(R.id.logoutbtn);
        homing = findViewById(R.id.homing);
        money = findViewById(R.id.money);
        profile456 = findViewById(R.id.profile456);
        developer123 = findViewById(R.id.developer123);

        // Tab array + highlight current tab
        allTabs = new ImageView[] { homing, money, profile456, developer123 };

        // Logout Button - show custom dialog
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(log_outpage.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                showLogoutDialog();
            }
        });

        // Tab navigation click listeners
        homing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(log_outpage.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                setActiveTab(homing);
                startActivity(new Intent(log_outpage.this, homepage.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(log_outpage.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                setActiveTab(money);
                startActivity(new Intent(log_outpage.this, moneyhand.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        profile456.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(log_outpage.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                setActiveTab(profile456);
                startActivity(new Intent(log_outpage.this, profilepage.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        developer123.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation btnAnim = AnimationUtils.loadAnimation(log_outpage.this, R.anim.button_press);
                view.startAnimation(btnAnim);
                setActiveTab(developer123);
                startActivity(new Intent(log_outpage.this, developer.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        // Animate dialog entrance
        View dialogView = dialog.getWindow().getDecorView();
        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        dialogView.startAnimation(scaleAnim);

        Button cancelBtn = dialog.findViewById(R.id.dialogCancel);
        Button logoutBtn = dialog.findViewById(R.id.dialogLogout);

        cancelBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        logoutBtn.setOnClickListener(v -> {
            Animation btnAnim = AnimationUtils.loadAnimation(this, R.anim.button_press);
            v.startAnimation(btnAnim);
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(log_outpage.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        dialog.show();
    }

    // Set active tab background
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
