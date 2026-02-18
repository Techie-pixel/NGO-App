package com.example.application;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.HashMap;

public class feedback extends AppCompatActivity {

    EditText nameInput, improvementInput, otherSuggestions;
    Button submitBtn;

    FirebaseAuth auth;
    DatabaseReference feedbackRef;

    TextView welcomeFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback); // Use your actual XML name

        // UI binding
        nameInput = findViewById(R.id.nameInput);
        improvementInput = findViewById(R.id.improvementInput);
        otherSuggestions = findViewById(R.id.otherSuggestions);
        submitBtn = findViewById(R.id.submitBtn);
        welcomeFeedback = findViewById(R.id.welcome_feedback);

        // Firebase setup
        auth = FirebaseAuth.getInstance();
        feedbackRef = FirebaseDatabase.getInstance().getReference("Feedback");

        // Fetch user name
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("users").child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child("name").getValue(String.class);
                            if (name != null && !name.isEmpty()) {
                                welcomeFeedback.setText("Welcome, " + name);
                            } else {
                                welcomeFeedback.setText("Welcome, Guest");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            welcomeFeedback.setText("Welcome, Guest");
                        }
                    });
        } else {
            welcomeFeedback.setText("Welcome, Guest");
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(feedback.this, R.anim.button_press));
                submitFeedback();
            }
        });
    }

    private void submitFeedback() {
        String name = nameInput.getText().toString().trim();
        String improvement = improvementInput.getText().toString().trim();
        String suggestion = otherSuggestions.getText().toString().trim();
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "guest";

        // Validation
        if (name.isEmpty()) {
            nameInput.setError("Enter your name");
            return;
        }

        // HashMap for feedback
        HashMap<String, Object> feedbackMap = new HashMap<>();
        feedbackMap.put("name", name);
        feedbackMap.put("improvement", improvement);
        feedbackMap.put("suggestion", suggestion);
        feedbackMap.put("timestamp", System.currentTimeMillis());

        // Push data under user UID with auto ID
        feedbackRef.child(uid).push().setValue(feedbackMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                    // Clear fields
                    nameInput.setText("");
                    improvementInput.setText("");
                    otherSuggestions.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send feedback: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
