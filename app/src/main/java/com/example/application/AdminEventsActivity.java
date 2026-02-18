package com.example.application;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminEventsActivity extends AppCompatActivity {

    private ImageView eventImagePreview;
    private EditText eventTitle, eventDate, eventDescription;
    private Button btnPickImage, btnUpload;
    private ProgressBar uploadProgress;
    private RecyclerView eventsRecyclerView;

    private Uri selectedImageUri = null;
    private DatabaseReference databaseReference;

    private List<Map<String, String>> eventsList = new ArrayList<>();
    private List<String> eventKeys = new ArrayList<>();
    private EventsAdapter adapter;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    eventImagePreview.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events);

        eventImagePreview = findViewById(R.id.eventImagePreview);
        eventTitle = findViewById(R.id.eventTitle);
        eventDate = findViewById(R.id.eventDate);
        eventDescription = findViewById(R.id.eventDescription);
        btnPickImage = findViewById(R.id.btnPickEventImage);
        btnUpload = findViewById(R.id.btnUploadEvent);
        uploadProgress = findViewById(R.id.uploadProgress);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventsAdapter();
        eventsRecyclerView.setAdapter(adapter);

        btnPickImage.setOnClickListener(v -> {
            Animation btnAnim = AnimationUtils.loadAnimation(this, R.anim.button_press);
            v.startAnimation(btnAnim);
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnUpload.setOnClickListener(v -> {
            Animation btnAnim = AnimationUtils.loadAnimation(this, R.anim.button_press);
            v.startAnimation(btnAnim);
            uploadEvent();
        });

        loadEvents();
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null)
                inputStream.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadEvent() {
        String title = eventTitle.getText().toString().trim();
        String date = eventDate.getText().toString().trim();
        String description = eventDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            eventTitle.setError("Title is required");
            return;
        }
        if (TextUtils.isEmpty(date)) {
            eventDate.setError("Date is required");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            eventDescription.setError("Description is required");
            return;
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadProgress.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        String base64Image = convertImageToBase64(selectedImageUri);
        if (base64Image == null) {
            uploadProgress.setVisibility(View.GONE);
            btnUpload.setEnabled(true);
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("date", date);
        eventData.put("description", description);
        eventData.put("imageBase64", base64Image);

        databaseReference.push().setValue(eventData)
                .addOnSuccessListener(aVoid -> {
                    uploadProgress.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(this, "Event uploaded successfully!", Toast.LENGTH_SHORT).show();
                    eventTitle.setText("");
                    eventDate.setText("");
                    eventDescription.setText("");
                    eventImagePreview.setImageResource(0);
                    selectedImageUri = null;
                })
                .addOnFailureListener(e -> {
                    uploadProgress.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(this, "Failed to save event: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteEvent(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String key = eventKeys.get(position);
                    databaseReference.child(key).removeValue()
                            .addOnSuccessListener(
                                    aVoid -> Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(
                                    e -> Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadEvents() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsList.clear();
                eventKeys.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, String> event = new HashMap<>();
                    event.put("title", ds.child("title").getValue(String.class));
                    event.put("date", ds.child("date").getValue(String.class));
                    event.put("description", ds.child("description").getValue(String.class));
                    event.put("imageBase64", ds.child("imageBase64").getValue(String.class));
                    eventsList.add(event);
                    eventKeys.add(ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_event, parent, false);
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
            holder.deleteBtn.setOnClickListener(v -> deleteEvent(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image, deleteBtn;
            TextView title, date, description;

            ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.eventImage);
                title = itemView.findViewById(R.id.eventTitle);
                date = itemView.findViewById(R.id.eventDate);
                description = itemView.findViewById(R.id.eventDescription);
                deleteBtn = itemView.findViewById(R.id.btnDeleteEvent);
            }
        }
    }
}
