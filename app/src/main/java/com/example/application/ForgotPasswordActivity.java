package com.example.application;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText resetEmail;
    Button sendResetLink;
    TextView backToLogin, resetStatusText;
    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        resetEmail = findViewById(R.id.resetEmail);
        sendResetLink = findViewById(R.id.sendResetLink);
        backToLogin = findViewById(R.id.backToLogin);
        resetStatusText = findViewById(R.id.resetStatusText);

        mauth = FirebaseAuth.getInstance();

        // Back to login
        backToLogin.setOnClickListener(view -> {
            Animation btnAnim = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.button_press);
            view.startAnimation(btnAnim);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Send password reset email
        sendResetLink.setOnClickListener(view -> {
            // Button press animation
            Animation btnAnim = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.button_press);
            view.startAnimation(btnAnim);

            String email = resetEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                resetEmail.setError("Please enter your email address");
                resetEmail.requestFocus();
                return;
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                resetEmail.setError("Please enter a valid email address");
                resetEmail.requestFocus();
                return;
            }

            // Disable button to prevent multiple clicks
            sendResetLink.setEnabled(false);
            sendResetLink.setText("Sending...");

            mauth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            resetStatusText.setVisibility(View.VISIBLE);
                            resetStatusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                            resetStatusText.setText("✓ Password reset link sent to your email!");
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Reset link sent! Check your email inbox.",
                                    Toast.LENGTH_LONG).show();
                            sendResetLink.setText("Link Sent ✓");
                        } else {
                            resetStatusText.setVisibility(View.VISIBLE);
                            resetStatusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            resetStatusText.setText("Failed: " +
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Failed to send reset email. Please try again.",
                                    Toast.LENGTH_LONG).show();
                            sendResetLink.setEnabled(true);
                            sendResetLink.setText("Send Reset Link");
                        }
                    });
        });
    }
}
