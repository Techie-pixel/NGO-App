package com.example.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class signin extends AppCompatActivity {

    EditText name, email, password, mobile;
    Button signup;
    TextView signinlogin;
    ImageView eyeToggleSignup;
    ProgressBar signupProgress;
    boolean isPasswordVisible = false;
    private FirebaseAuth mauth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        // Initialize views
        name = findViewById(R.id.username1);
        email = findViewById(R.id.username2);
        password = findViewById(R.id.username3);
        mobile = findViewById(R.id.mobile123);
        signup = findViewById(R.id.button1);
        signinlogin = findViewById(R.id.signinlogin);
        eyeToggleSignup = findViewById(R.id.eyeToggleSignup);
        signupProgress = findViewById(R.id.signupProgress);

        // Firebase
        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Password visibility toggle
        eyeToggleSignup.setOnClickListener(v -> {
            if (isPasswordVisible) {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eyeToggleSignup.setImageResource(R.drawable.ic_eye_closed);
                isPasswordVisible = false;
            } else {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eyeToggleSignup.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            password.setSelection(password.getText().length());
        });

        // Register click with animation
        signup.setOnClickListener(v -> {
            Animation btnAnim = AnimationUtils.loadAnimation(signin.this, R.anim.button_press);
            v.startAnimation(btnAnim);
            registeruser();
        });

        // Login intent click
        signinlogin.setOnClickListener(view -> {
            Animation btnAnim = AnimationUtils.loadAnimation(signin.this, R.anim.button_press);
            view.startAnimation(btnAnim);
            Intent intent = new Intent(signin.this, login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void registeruser() {
        String username1 = name.getText().toString().trim();
        String username2 = email.getText().toString().trim();
        String username3 = password.getText().toString().trim();
        String username4 = mobile.getText().toString().trim();

        if (TextUtils.isEmpty(username1)) {
            name.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(username2) || !Patterns.EMAIL_ADDRESS.matcher(username2).matches()) {
            email.setError("Valid email is required");
            return;
        }

        if (TextUtils.isEmpty(username3)) {
            password.setError("Password is required");
            return;
        }

        if (username3.length() < 6) {
            password.setError("Password must be at least 6 characters");
            return;
        }

        if (TextUtils.isEmpty(username4)) {
            mobile.setError("Mobile number is required");
            return;
        }

        if (username4.length() < 10) {
            mobile.setError("Mobile number should be at least 10 digits");
            return;
        }

        // Show loading
        signupProgress.setVisibility(View.VISIBLE);
        signup.setEnabled(false);
        signup.setText("Creating account...");

        // Create user in Firebase
        mauth.createUserWithEmailAndPassword(username2, username3)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mauth.getCurrentUser();
                        if (user != null) {
                            String userid = user.getUid();

                            // Clean user data
                            HashMap<String, String> userData = new HashMap<>();
                            userData.put("name", username1);
                            userData.put("email", username2);
                            userData.put("mobile_no", username4);

                            // Save to database
                            databaseReference.child(userid).setValue(userData)
                                    .addOnCompleteListener(task1 -> {
                                        signupProgress.setVisibility(View.GONE);
                                        signup.setEnabled(true);
                                        signup.setText("Sign Up");
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Registration successful",
                                                    Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(signin.this, homepage.class));
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Data write failed",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        signupProgress.setVisibility(View.GONE);
                        signup.setEnabled(true);
                        signup.setText("Sign Up");
                        Toast.makeText(getApplicationContext(), "Signup failed: " + task.getException(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
