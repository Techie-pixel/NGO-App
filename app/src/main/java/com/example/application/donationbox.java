package com.example.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class donationbox extends AppCompatActivity {

    SeekBar amountseekbar;
    TextView selectedamount, wel1;
    EditText messageinput;
    Button donatebutton, donationhistory;

    FirebaseAuth mauth;
    DatabaseReference data;

    int selectedvalue = 0;
    String name1 = "Guest"; // default guest name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donationbox);

        // Initialize views
        amountseekbar = findViewById(R.id.selectdonation);
        selectedamount = findViewById(R.id.inr123);
        messageinput = findViewById(R.id.enteryourmsg);
        donatebutton = findViewById(R.id.donatehere);
        donationhistory = findViewById(R.id.donationhistory);
        wel1 = findViewById(R.id.welcome);

        mauth = FirebaseAuth.getInstance();
        data = FirebaseDatabase.getInstance().getReference();

        // Seekbar settings
        amountseekbar.setMax(100);
        amountseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedvalue = progress;
                selectedamount.setText("₹ " + selectedvalue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Load user's name or show "Guest"
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            data.child("users").child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fetchedName = snapshot.getValue(String.class);
                    if (fetchedName != null && !fetchedName.trim().isEmpty()) {
                        name1 = fetchedName;
                    }
                    wel1.setText("Welcome, " + name1 + "!");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed to load username", Toast.LENGTH_SHORT).show();
                    wel1.setText("Welcome, Guest!");
                }
            });
        } else {
            wel1.setText("Welcome, Guest!");
        }

        // Donation button
        donatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(donationbox.this, R.anim.button_press));
                if (selectedvalue == 0) {
                    Toast.makeText(getApplicationContext(), "Please select a donation amount", Toast.LENGTH_LONG)
                            .show();
                } else {
                    String message = messageinput.getText().toString().trim();
                    processPayment(name1, selectedvalue, message);
                }
            }
        });

        // Donation history button
        donationhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(donationbox.this, R.anim.button_press));
                Intent intent = new Intent(donationbox.this, donationhistory.class);
                startActivity(intent);
            }
        });
    }

    // Process payment and reset form
    private void processPayment(String name, int amount, String message) {
        Toast.makeText(getApplicationContext(), "Payment Successful: ₹" + amount, Toast.LENGTH_LONG).show();

        // Record donation in Firebase
        String currentdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String userId = (mauth.getCurrentUser() != null) ? mauth.getCurrentUser().getUid() : "guest";
        DatabaseReference data1 = FirebaseDatabase.getInstance().getReference("Donations");
        String donationId = data1.push().getKey();

        if (donationId != null) {
            HashMap<String, Object> donationdata = new HashMap<>();
            donationdata.put("Name", name);
            donationdata.put("Amount", amount);
            donationdata.put("Message", message);
            donationdata.put("Date", currentdate);
            donationdata.put("userId", userId);

            data1.child(donationId).setValue(donationdata);
            Toast.makeText(getApplicationContext(), "Donation recorded successfully!", Toast.LENGTH_SHORT).show();

            // Reset form after successful donation
            resetForm();
        }
    }

    // Reset form method
    private void resetForm() {
        amountseekbar.setProgress(0);
        selectedvalue = 0;
        selectedamount.setText("₹ 0");
        messageinput.setText("");
    }
}