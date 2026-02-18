package com.example.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profilepage extends AppCompatActivity {

    TextView email123, mobile_no, name;
    DatabaseReference databaseReference;
    FirebaseAuth mauth;

    ImageView homing, money, profile456, developer123;
    ImageView[] allTabs;
    Button logoutButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profilepage);

        // UI References
        email123 = findViewById(R.id.email1);
        mobile_no = findViewById(R.id.mobile_no1233);
        name = findViewById(R.id.name345);
        logoutButton = findViewById(R.id.logoutButton);

        homing = findViewById(R.id.homing);
        money = findViewById(R.id.money);
        profile456 = findViewById(R.id.profile456);
        developer123 = findViewById(R.id.developer123);

        allTabs = new ImageView[] { homing, money, profile456, developer123 };
        setActiveTab(profile456);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mauth = FirebaseAuth.getInstance();

        // Fetch data from Firebase Realtime Database
        FirebaseUser currentuser = mauth.getCurrentUser();
        if (currentuser != null) {
            String userId = currentuser.getUid();
            DatabaseReference userRef = databaseReference.child("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = snapshot.child("email").getValue(String.class);
                    String mobile = snapshot.child("mobile_no").getValue(String.class);
                    String namevalue = snapshot.child("name").getValue(String.class);

                    email123.setText("Email: " + email);
                    mobile_no.setText("Mobile: " + mobile);
                    name.setText("Name: " + namevalue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(profilepage.this, "Failed to load the data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(profilepage.this, R.anim.button_press));
                showLogoutDialog();
            }
        });

        // Tab click listeners
        homing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(profilepage.this, R.anim.button_press));
                setActiveTab(homing);
                startActivity(new Intent(profilepage.this, homepage.class));
            }
        });

        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(profilepage.this, R.anim.button_press));
                setActiveTab(money);
                startActivity(new Intent(profilepage.this, moneyhand.class));
            }
        });

        profile456.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(profilepage.this, R.anim.button_press));
                setActiveTab(profile456);
                Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_SHORT).show();
            }
        });

        developer123.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(profilepage.this, R.anim.button_press));
                setActiveTab(developer123);
                startActivity(new Intent(profilepage.this, developer.class));
            }
        });

    }

    private void setActiveTab(ImageView selectedTab) {
        for (ImageView tab : allTabs) {
            tab.setBackgroundResource(R.drawable.inactive_tab_bg);
        }
        selectedTab.setBackgroundResource(R.drawable.active_tab_bg);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            mauth.signOut();
            Toast.makeText(profilepage.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(profilepage.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}