package com.example.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import java.util.Objects;

public class login extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView redirectToRegister, forgotPassword;
    ImageView eyeToggle;
    ProgressBar loginProgress;
    boolean isPasswordVisible = false;

    FirebaseAuth mauth;

    // TODO: Replace with your admin email and password
    private static final String ADMIN_EMAIL = "your_admin_email@gmail.com";
    private static final String ADMIN_PASSWORD = "your_admin_password";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password1);
        login = findViewById(R.id.login1);
        redirectToRegister = findViewById(R.id.guest1);
        forgotPassword = findViewById(R.id.forgotPassword);
        eyeToggle = findViewById(R.id.eyeToggle);
        loginProgress = findViewById(R.id.loginProgress);

        mauth = FirebaseAuth.getInstance();

        // Password visibility toggle
        eyeToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eyeToggle.setImageResource(R.drawable.ic_eye_closed);
                isPasswordVisible = false;
            } else {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eyeToggle.setImageResource(R.drawable.ic_eye_open);
                isPasswordVisible = true;
            }
            password.setSelection(password.getText().length());
        });

        // Forgot Password click listener
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Button press animation
                Animation btnAnim = AnimationUtils.loadAnimation(login.this, R.anim.button_press);
                v.startAnimation(btnAnim);

                String useremail = email.getText().toString().trim();
                String userpass = password.getText().toString().trim();

                // Validation
                if (TextUtils.isEmpty(useremail)) {
                    email.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(userpass)) {
                    password.setError("Password is required");
                    return;
                }

                // Show loading
                loginProgress.setVisibility(View.VISIBLE);
                login.setEnabled(false);
                login.setText("Logging in...");

                // Admin login check
                if (useremail.equals(ADMIN_EMAIL) && userpass.equals(ADMIN_PASSWORD)) {
                    loginProgress.setVisibility(View.GONE);
                    login.setEnabled(true);
                    login.setText("Login");

                    // Save admin session
                    SharedPreferences prefs = getSharedPreferences("AdminPrefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("isAdminLoggedIn", true).apply();

                    Toast.makeText(getApplicationContext(), "Admin login successful", Toast.LENGTH_LONG).show();
                    Intent mm = new Intent(login.this, adminpanel.class);
                    startActivity(mm);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    // Firebase Authentication
                    mauth.signInWithEmailAndPassword(useremail, userpass)
                            .addOnCompleteListener(task -> {
                                loginProgress.setVisibility(View.GONE);
                                login.setEnabled(true);
                                login.setText("Login");
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG)
                                            .show();
                                    startActivity(new Intent(login.this, homepage.class));
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        redirectToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, signin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }
}
