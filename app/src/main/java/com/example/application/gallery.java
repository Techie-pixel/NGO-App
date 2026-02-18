package com.example.application;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class gallery extends AppCompatActivity {

    TextView welcomeGallery;
    DatabaseReference databaseReference;
    RecyclerView userGalleryRecyclerView;
    List<Map<String, String>> galleryList = new ArrayList<>();
    UserGalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        welcomeGallery = findViewById(R.id.welcome_gallery);
        userGalleryRecyclerView = findViewById(R.id.userGalleryRecyclerView);

        // Setup RecyclerView for dynamic gallery
        userGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserGalleryAdapter();
        userGalleryRecyclerView.setAdapter(adapter);

        // Check if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null && !name.isEmpty()) {
                        welcomeGallery.setText("Welcome, " + name);
                    } else {
                        welcomeGallery.setText("Welcome, Guest");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    welcomeGallery.setText("Welcome, Guest");
                }
            });
        } else {
            welcomeGallery.setText("Welcome, Guest");
        }

        // Load gallery from Firebase
        loadGallery();
    }

    private void loadGallery() {
        DatabaseReference galleryRef = FirebaseDatabase.getInstance().getReference("gallery");
        galleryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                galleryList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, String> item = new HashMap<>();
                    item.put("caption", ds.child("caption").getValue(String.class));
                    item.put("imageBase64", ds.child("imageBase64").getValue(String.class));
                    galleryList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(gallery.this, "Failed to load gallery", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UserGalleryAdapter extends RecyclerView.Adapter<UserGalleryAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery_user, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, String> item = galleryList.get(position);
            holder.caption.setText(item.get("caption"));
            String base64 = item.get("imageBase64");
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
            return galleryList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView caption;

            ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.userGalleryImage);
                caption = itemView.findViewById(R.id.userGalleryCaption);
            }
        }
    }
}