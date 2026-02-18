package com.example.application;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class aboutus extends AppCompatActivity {

    TextView welcomeguest;
    DatabaseReference databaseReference;
    RecyclerView userEventsRecyclerView;
    List<Map<String, String>> eventsList = new ArrayList<>();
    UserEventsAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aboutus);

        welcomeguest = findViewById(R.id.welcomeguest);
        userEventsRecyclerView = findViewById(R.id.userEventsRecyclerView);

        // Setup RecyclerView for dynamic events
        userEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserEventsAdapter();
        userEventsRecyclerView.setAdapter(adapter);

        // Check if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null && !name.isEmpty()) {
                        welcomeguest.setText("Welcome, " + name + "!!");
                    } else {
                        welcomeguest.setText("Welcome, User!!!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    welcomeguest.setText("Welcome, Guest!!!");
                }
            });
        } else {
            welcomeguest.setText("Welcome, Guest!!!");
        }

        // Load events from Firebase
        loadEvents();
    }

    private void loadEvents() {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, String> event = new HashMap<>();
                    event.put("title", ds.child("title").getValue(String.class));
                    event.put("date", ds.child("date").getValue(String.class));
                    event.put("description", ds.child("description").getValue(String.class));
                    event.put("imageBase64", ds.child("imageBase64").getValue(String.class));
                    eventsList.add(event);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(aboutus.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UserEventsAdapter extends RecyclerView.Adapter<UserEventsAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_user, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, String> event = eventsList.get(position);
            holder.title.setText(event.get("title"));
            holder.date.setText(event.get("date"));
            holder.description.setText(event.get("description"));
            String base64 = event.get("imageBase64");
            if (base64 != null && !base64.isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    holder.image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    holder.image.setImageResource(0);
                }
            }
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title, date, description;

            ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.userEventImage);
                title = itemView.findViewById(R.id.userEventTitle);
                date = itemView.findViewById(R.id.userEventDate);
                description = itemView.findViewById(R.id.userEventDescription);
            }
        }
    }
}
