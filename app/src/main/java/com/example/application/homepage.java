package com.example.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class homepage extends AppCompatActivity {

    TextView wel;
    LinearLayout donation1, gallery, feedback, linear, events, volu;
    ImageView homing, money, profile456, developer123;
    ImageView[] allTabs;
    FirebaseAuth mauth;
    DatabaseReference databaseReference;
    private ViewFlipper viewflipper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Initialize views
        wel = findViewById(R.id.welcome);
        donation1 = findViewById(R.id.donationbox);
        linear = findViewById(R.id.contact);
        gallery = findViewById(R.id.gallery1);
        events = findViewById(R.id.events);
        volu = findViewById(R.id.volu);
        feedback = findViewById(R.id.feedback);
        viewflipper = findViewById(R.id.homefliper);

        // Navigation icons
        homing = findViewById(R.id.homing);
        money = findViewById(R.id.money);
        profile456 = findViewById(R.id.profile456);
        developer123 = findViewById(R.id.developer123);

        // Firebase
        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Start flipper
        if (viewflipper != null) {
            viewflipper.startFlipping();
        }

        // Setup tabs
        allTabs = new ImageView[] { homing, money, profile456, developer123 };
        setActiveTab(homing);

        fetchUserName();
        setupClickListeners();

        // Animate grid cards with staggered entrance
        animateGridCards();
    }

    private void animateGridCards() {
        LinearLayout[] cards = { events, gallery, volu, linear, donation1, feedback };
        Handler handler = new Handler();
        for (int i = 0; i < cards.length; i++) {
            final LinearLayout card = cards[i];
            if (card != null) {
                card.setAlpha(0f);
                handler.postDelayed(() -> {
                    card.setAlpha(1f);
                    Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                    card.startAnimation(slideUp);
                }, (long) (i * 120)); // 120ms stagger between each card
            }
        }
    }

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

    private void fetchUserName() {
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference.child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name1 = snapshot.getValue(String.class);
                    if (name1 != null) {
                        wel.setText("Welcome, " + name1 + "!");
                    } else {
                        wel.setText("Welcome, Guest!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed to load username", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            wel.setText("Welcome, Guest!");
        }
    }

    private void setupClickListeners() {
        // Navigation tabs
        if (homing != null) {
            homing.setOnClickListener(view -> {
                animateCard(view);
                setActiveTab(homing);
                Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
            });
        }

        if (money != null) {
            money.setOnClickListener(view -> {
                animateCard(view);
                setActiveTab(money);
                startActivity(new Intent(homepage.this, moneyhand.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }

        if (profile456 != null) {
            profile456.setOnClickListener(view -> {
                animateCard(view);
                setActiveTab(profile456);
                startActivity(new Intent(homepage.this, profilepage.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }

        if (developer123 != null) {
            developer123.setOnClickListener(view -> {
                animateCard(view);
                setActiveTab(developer123);
                startActivity(new Intent(homepage.this, developer.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }

        // Main menu items with press animation
        if (linear != null) {
            linear.setOnClickListener(view -> {
                animateCard(view);
                startActivity(new Intent(homepage.this, contactus.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }

        if (donation1 != null) {
            donation1.setOnClickListener(view -> {
                animateCard(view);
                startActivity(new Intent(homepage.this, donationbox.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }

        if (gallery != null) {
            gallery.setOnClickListener(view -> {
                animateCard(view);
                startActivity(new Intent(homepage.this, gallery.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }

        if (events != null) {
            events.setOnClickListener(view -> {
                animateCard(view);
                startActivity(new Intent(homepage.this, aboutus.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }

        if (volu != null) {
            volu.setOnClickListener(view -> {
                animateCard(view);
                startActivity(new Intent(homepage.this, volunteers.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }

        if (feedback != null) {
            feedback.setOnClickListener(view -> {
                animateCard(view);
                startActivity(new Intent(homepage.this, feedback.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
        }
    }

    private void animateCard(View view) {
        Animation btnAnim = AnimationUtils.loadAnimation(this, R.anim.button_press);
        view.startAnimation(btnAnim);
    }
}