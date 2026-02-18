package com.example.application;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class alluserlist extends AppCompatActivity {

    ListView userlistview;
    DatabaseReference databaseReference;
    List<Map<String, String>> userList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alluserlist);

        userlistview = findViewById(R.id.userlistview);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String name = userSnap.child("name").getValue(String.class);
                    String phone = userSnap.child("mobile_no").getValue(String.class);
                    String email = userSnap.child("email").getValue(String.class);

                    Map<String, String> user = new HashMap<>();
                    user.put("name", name != null ? name : "N/A");
                    user.put("phone", phone != null ? phone : "N/A");
                    user.put("email", email != null ? email : "N/A");
                    userList.add(user);
                }

                SimpleAdapter adapter = new SimpleAdapter(
                        alluserlist.this,
                        userList,
                        R.layout.itemlist,
                        new String[] { "name", "phone", "email" },
                        new int[] { R.id.nametext, R.id.phonetext, R.id.emailtext });
                userlistview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load users", Toast.LENGTH_LONG).show();
            }
        });

        // Click to show user detail dialog
        userlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.startAnimation(
                        android.view.animation.AnimationUtils.loadAnimation(alluserlist.this, R.anim.button_press));
                Map<String, String> user = userList.get(position);

                View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);
                // Reuse dialog_rounded_bg but build custom content
                AlertDialog.Builder builder = new AlertDialog.Builder(alluserlist.this);
                builder.setTitle("User Details");
                builder.setMessage(
                        "Name: " + user.get("name") + "\n\n" +
                                "Phone: " + user.get("phone") + "\n\n" +
                                "Email: " + user.get("email"));
                builder.setPositiveButton("Close", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
