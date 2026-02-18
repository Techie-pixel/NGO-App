package com.example.application;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;

public class contactus extends AppCompatActivity {

    FirebaseAuth mauth;
    DatabaseReference data;
    EditText ctname, ctmobile, ctquery;
    Button btnsub;
    TextView wel1;

    DatabaseReference databaseReference;

    String name1 = "Guest"; // default name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contactus);

        ctname = findViewById(R.id.contactname);
        ctmobile = findViewById(R.id.mobile);
        ctquery = findViewById(R.id.query);
        btnsub = findViewById(R.id.buttoncontact);
        wel1 = findViewById(R.id.welcome1);

        mauth = FirebaseAuth.getInstance();
        data = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("contactdata");

        // Check user login status
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
                    wel1.setText("Welcome, " + name1 + "!!");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    wel1.setText("Welcome, Guest!!");
                }
            });
        } else {
            wel1.setText("Welcome, Guest!!!");
        }

        btnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(contactus.this, R.anim.button_press));
                contactus();
            }
        });
    }

    private void contactus() {
        String name = ctname.getText().toString().trim();
        String mobile = ctmobile.getText().toString().trim();
        String query = ctquery.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            ctname.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            ctmobile.setError("Mobile no is required");
            return;
        }

        if (mobile.length() < 10) {
            ctmobile.setError("Mobile no should be at least 10 digits");
            return;
        }

        if (TextUtils.isEmpty(query)) {
            ctquery.setError("Please enter your query");
            return;
        }

        String id = databaseReference.push().getKey();
        HashMap<String, String> contactmap = new HashMap<>();
        contactmap.put("Name", name);
        contactmap.put("Mobile", mobile);
        contactmap.put("Query", query);

        assert id != null;
        databaseReference.child(id).setValue(contactmap).addOnSuccessListener(aVoid -> {
            Toast.makeText(getApplicationContext(), "Query submitted successfully", Toast.LENGTH_LONG).show();
            ctname.setText("");
            ctmobile.setText("");
            ctquery.setText("");
        });
    }
}
