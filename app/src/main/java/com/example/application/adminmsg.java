package com.example.application;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

public class adminmsg extends AppCompatActivity {

    private static final String TAG = "AdminMsg";

    ListView msglistview;
    List<Map<String, String>> msgList = new ArrayList<>();
    int pendingLoads = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adminmsg);

        msglistview = findViewById(R.id.msglistview);

        // Load messages from all three sources
        loadContactMessages();
        loadFeedbackMessages();
        loadVolunteerMessages();
    }

    // contactus.java writes to "contactdata" node with keys: Name, Mobile, Query
    private void loadContactMessages() {
        pendingLoads++;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("contactdata");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "contactdata: " + snapshot.getChildrenCount() + " entries");
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Map<String, String> msg = new HashMap<>();
                    String name = snap.child("Name").getValue(String.class);
                    if (name == null)
                        name = snap.child("name").getValue(String.class);
                    if (name == null)
                        name = "Unknown";

                    String query = snap.child("Query").getValue(String.class);
                    if (query == null)
                        query = snap.child("query").getValue(String.class);

                    String mobile = snap.child("Mobile").getValue(String.class);
                    if (mobile == null)
                        mobile = snap.child("mobile").getValue(String.class);

                    String message = (query != null ? query : "")
                            + (mobile != null ? " | Mobile: " + mobile : "");
                    if (message.trim().isEmpty())
                        message = "No message";

                    msg.put("name", name);
                    msg.put("message", message);
                    msg.put("source", "Contact");
                    msgList.add(msg);
                }
                checkAndUpdate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "contactdata error: " + error.getMessage());
                checkAndUpdate();
            }
        });
    }

    // feedback.java writes to "Feedback/{uid}/{pushId}" with keys: name,
    // improvement, suggestion
    private void loadFeedbackMessages() {
        pendingLoads++;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Feedback");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Feedback: " + snapshot.getChildrenCount() + " user entries");
                // Feedback is nested: Feedback -> uid -> pushId -> data
                for (DataSnapshot uidSnap : snapshot.getChildren()) {
                    for (DataSnapshot feedbackSnap : uidSnap.getChildren()) {
                        Map<String, String> msg = new HashMap<>();
                        String name = feedbackSnap.child("name").getValue(String.class);
                        if (name == null)
                            name = "Unknown";

                        String improvement = feedbackSnap.child("improvement").getValue(String.class);
                        String suggestion = feedbackSnap.child("suggestion").getValue(String.class);

                        StringBuilder sb = new StringBuilder();
                        if (improvement != null && !improvement.trim().isEmpty())
                            sb.append("Improvement: ").append(improvement);
                        if (suggestion != null && !suggestion.trim().isEmpty()) {
                            if (sb.length() > 0)
                                sb.append(" | ");
                            sb.append("Suggestion: ").append(suggestion);
                        }
                        String message = sb.length() > 0 ? sb.toString() : "No message";

                        msg.put("name", name);
                        msg.put("message", message);
                        msg.put("source", "Feedback");
                        msgList.add(msg);
                    }
                }
                checkAndUpdate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Feedback error: " + error.getMessage());
                checkAndUpdate();
            }
        });
    }

    // volunteers.java writes to "volunteer" node with keys: name, email, mobile
    private void loadVolunteerMessages() {
        pendingLoads++;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("volunteer");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "volunteer: " + snapshot.getChildrenCount() + " entries");
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Map<String, String> msg = new HashMap<>();
                    String name = snap.child("name").getValue(String.class);
                    if (name == null)
                        name = "Unknown";

                    String email = snap.child("email").getValue(String.class);
                    String mobile = snap.child("mobile").getValue(String.class);

                    String message = "Email: " + (email != null ? email : "N/A")
                            + " | Mobile: " + (mobile != null ? mobile : "N/A");

                    msg.put("name", name);
                    msg.put("message", message);
                    msg.put("source", "Volunteer");
                    msgList.add(msg);
                }
                checkAndUpdate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "volunteer error: " + error.getMessage());
                checkAndUpdate();
            }
        });
    }

    private void checkAndUpdate() {
        pendingLoads--;
        if (pendingLoads == 0) {
            updateListView();
        }
    }

    private void updateListView() {
        if (msgList.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No messages found", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleAdapter adapter = new SimpleAdapter(
                adminmsg.this,
                msgList,
                R.layout.msg_list,
                new String[] { "name", "message", "source" },
                new int[] { R.id.nametext, R.id.msgtext, R.id.sourcetext });
        msglistview.setAdapter(adapter);
    }
}
