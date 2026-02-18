package com.example.application;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class donationhistory extends AppCompatActivity {

    private LinearLayout donationContainer;
    private TextView totalDonatedText;
    private DatabaseReference donationRef, usersRef;
    private int totalDonated = 0;

    private String currentUserId = "guest";
    private String currentUserName = "Guest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donationhistory);

        donationContainer = findViewById(R.id.donationContainer);
        totalDonatedText = findViewById(R.id.totalDonated);

        donationRef = FirebaseDatabase.getInstance().getReference("Donations");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            // Fetch user name then load donations
            usersRef.child(currentUserId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null && !name.trim().isEmpty()) {
                        currentUserName = name;
                    }
                    fetchDonationHistory();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    fetchDonationHistory();
                }
            });
        } else {
            // Guest user
            currentUserId = "guest";
            currentUserName = "Guest";
            fetchDonationHistory();
        }
    }

    private void fetchDonationHistory() {
        donationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalDonated = 0;
                donationContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "No donation history found", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean foundAny = false;

                for (DataSnapshot donationSnap : snapshot.getChildren()) {
                    // Check if this donation belongs to the current user
                    String donationUserId = donationSnap.child("userId").getValue(String.class);
                    String donationName = donationSnap.child("Name").getValue(String.class);

                    boolean isMatch = false;

                    // Match by userId first (new donations)
                    if (donationUserId != null && donationUserId.equals(currentUserId)) {
                        isMatch = true;
                    }
                    // Fallback: match by name for old donations without userId
                    else if (donationUserId == null && donationName != null
                            && donationName.equalsIgnoreCase(currentUserName)) {
                        isMatch = true;
                    }

                    if (!isMatch)
                        continue;

                    Object amountObj = donationSnap.child("Amount").getValue();
                    String date = donationSnap.child("Date").getValue(String.class);
                    String message = donationSnap.child("Message").getValue(String.class);

                    if (amountObj != null && date != null) {
                        int amount = 0;

                        if (amountObj instanceof Long) {
                            amount = ((Long) amountObj).intValue();
                        } else if (amountObj instanceof Integer) {
                            amount = (Integer) amountObj;
                        } else if (amountObj instanceof Double) {
                            amount = (int) Math.round((Double) amountObj);
                        }

                        totalDonated += amount;
                        foundAny = true;

                        addDonationCard(donationName, date, amount, message);
                    }
                }

                if (!foundAny) {
                    Toast.makeText(getApplicationContext(), "No donations found for your account", Toast.LENGTH_SHORT)
                            .show();
                }

                totalDonatedText.setText("Total Donated: ₹" + totalDonated);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addDonationCard(String name, String date, int amount, String message) {
        // Create CardView
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 20);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(6f);
        cardView.setRadius(20f);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setContentPadding(30, 24, 30, 24);

        // Create inner LinearLayout
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Name TextView
        TextView nameText = new TextView(this);
        nameText.setText("Donor: " + (name != null ? name : "Guest"));
        nameText.setTextSize(20f);
        nameText.setTextColor(Color.BLACK);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);

        // Date TextView
        TextView dateText = new TextView(this);
        dateText.setText("Date: " + date);
        dateText.setTextSize(17f);
        dateText.setTextColor(Color.parseColor("#444444"));
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dateParams.setMargins(0, 12, 0, 0);
        dateText.setLayoutParams(dateParams);

        // Amount TextView
        TextView amountText = new TextView(this);
        amountText.setText("Amount: ₹" + amount);
        amountText.setTextSize(22f);
        amountText.setTextColor(Color.parseColor("#0077b6"));
        amountText.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams amountParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        amountParams.setMargins(0, 12, 0, 0);
        amountText.setLayoutParams(amountParams);

        // Add to inner layout
        innerLayout.addView(nameText);
        innerLayout.addView(dateText);
        innerLayout.addView(amountText);

        // Message TextView (if exists)
        if (message != null && !message.trim().isEmpty()) {
            TextView messageText = new TextView(this);
            messageText.setText("Message: \"" + message + "\"");
            messageText.setTextSize(16f);
            messageText.setTextColor(Color.parseColor("#555555"));
            messageText.setTypeface(null, android.graphics.Typeface.ITALIC);
            LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            messageParams.setMargins(0, 12, 0, 0);
            messageText.setLayoutParams(messageParams);
            innerLayout.addView(messageText);
        }

        // Add inner layout to card
        cardView.addView(innerLayout);

        // Add card to container
        donationContainer.addView(cardView);
    }
}