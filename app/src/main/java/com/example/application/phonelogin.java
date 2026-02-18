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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class phonelogin extends AppCompatActivity {

    EditText phoneInput;
    Button sendOtpButton;
    TextView backToLogin;
    FirebaseAuth mAuth;
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phonelogin);

        mAuth = FirebaseAuth.getInstance();
        phoneInput = findViewById(R.id.phoneinput);
        sendOtpButton = findViewById(R.id.sendotp);
        backToLogin = findViewById(R.id.backToLogin);

        // Back button
        backToLogin.setOnClickListener(view -> {
            Animation btnAnim = AnimationUtils.loadAnimation(phonelogin.this, R.anim.button_press);
            view.startAnimation(btnAnim);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Button press animation
                Animation btnAnim = AnimationUtils.loadAnimation(phonelogin.this, R.anim.button_press);
                view.startAnimation(btnAnim);

                String phone = phoneInput.getText().toString().trim();

                if (phone.isEmpty() || phone.length() < 10) {
                    phoneInput.setError("Enter a valid 10-digit phone number");
                    return;
                }

                // Disable button
                sendOtpButton.setEnabled(false);
                sendOtpButton.setText("Sending...");

                sendVerificationCode("+91" + phone);
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // Auto-verification
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(), "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            sendOtpButton.setEnabled(true);
            sendOtpButton.setText("Send OTP");
        }

        @Override
        public void onCodeSent(@NonNull String vId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            verificationId = vId;
            Toast.makeText(getApplicationContext(), "OTP Sent Successfully!", Toast.LENGTH_SHORT).show();
            sendOtpButton.setText("OTP Sent ✓");

            Intent intent = new Intent(phonelogin.this, otpverification.class);
            intent.putExtra("VerificationId", verificationId);
            intent.putExtra("phone", phoneInput.getText().toString().trim());
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };
}
