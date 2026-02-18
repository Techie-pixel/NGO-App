package com.example.application;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class volunteers extends AppCompatActivity {

    EditText edittextname, editetextmobile, edittextemail;
    FirebaseAuth mauth;
    Button submit;
    DatabaseReference databaseReference;
    TextView welcomeVolunteer;
    DatabaseReference userRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volunteers);

        edittextname = findViewById(R.id.editTextName);
        editetextmobile = findViewById(R.id.editTextMobile);
        edittextemail = findViewById(R.id.editTextEmail);

        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("volunteer");

        submit = findViewById(R.id.submitBtn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(volunteers.this, R.anim.button_press));
                volunteer();
            }
        });

        welcomeVolunteer = findViewById(R.id.welcome_volunteer);
        userRef = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null && !name.isEmpty()) {
                        welcomeVolunteer.setText("Welcome, " + name);
                    } else {
                        welcomeVolunteer.setText("Welcome, Guest");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    welcomeVolunteer.setText("Welcome, Guest");
                }
            });
        } else {
            welcomeVolunteer.setText("Welcome, Guest");
        }
    }

    private void volunteer() {
        String name = edittextname.getText().toString().trim();
        String mobile = editetextmobile.getText().toString().trim();
        String email = edittextemail.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            edittextname.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(mobile)) {
            editetextmobile.setError("Mobile number is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            edittextemail.setError("Email is required");
            return;
        }

        String id = databaseReference.push().getKey();
        HashMap<String, String> contactMap = new HashMap<>();
        contactMap.put("name", name);
        contactMap.put("email", email);
        contactMap.put("mobile", mobile);

        assert id != null;
        databaseReference.child(id).setValue(contactMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Submitted Successfully!", Toast.LENGTH_LONG).show();
                    edittextname.setText("");
                    edittextemail.setText("");
                    editetextmobile.setText("");
                });
    }
}
