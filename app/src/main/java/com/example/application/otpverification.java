package com.example.application;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class otpverification extends AppCompatActivity {

    EditText otpinput;
    Button verifyotp;
    TextView phoneDisplay;

    FirebaseAuth mauth;
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpverification);

        otpinput = findViewById(R.id.otpinput);
        verifyotp = findViewById(R.id.verifyotp);
        phoneDisplay = findViewById(R.id.phoneDisplay);
        mauth = FirebaseAuth.getInstance();

        verificationId = getIntent().getStringExtra("VerificationId");
        String phone = getIntent().getStringExtra("phone");

        // Display the phone number
        if (phone != null && !phone.isEmpty()) {
            phoneDisplay.setText("+91 " + phone);
        }

        verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Button press animation
                Animation btnAnim = AnimationUtils.loadAnimation(otpverification.this, R.anim.button_press);
                view.startAnimation(btnAnim);

                String code = otpinput.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    otpinput.setError("Enter a valid 6-digit OTP");
                    return;
                }

                verifyotp.setEnabled(false);
                verifyotp.setText("Verifying...");
                verifycode(code);
            }
        });
    }

    private void verifycode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(otpverification.this, homepage.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid OTP. Please try again.", Toast.LENGTH_LONG)
                                .show();
                        verifyotp.setEnabled(true);
                        verifyotp.setText("Verify OTP");
                    }
                });
    }
}