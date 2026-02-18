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

public class AdminGalleryActivity extends AppCompatActivity {

    private ImageView galleryImagePreview;
    private EditText galleryCaption;
    private Button btnPickImage, btnUpload;
    private ProgressBar uploadProgress;
    private RecyclerView galleryRecyclerView;

    private Uri selectedImageUri = null;
    private DatabaseReference databaseReference;

    private List<Map<String, String>> galleryList = new ArrayList<>();
    private List<String> galleryKeys = new ArrayList<>();
    private GalleryAdapter adapter;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    galleryImagePreview.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_gallery);

        galleryImagePreview = findViewById(R.id.galleryImagePreview);
        galleryCaption = findViewById(R.id.galleryCaption);
        btnPickImage = findViewById(R.id.btnPickGalleryImage);
        btnUpload = findViewById(R.id.btnUploadGallery);
        uploadProgress = findViewById(R.id.uploadProgress);
        galleryRecyclerView = findViewById(R.id.galleryRecyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference("gallery");

        galleryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GalleryAdapter();
        galleryRecyclerView.setAdapter(adapter);

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
            uploadGalleryItem();
        });

        loadGallery();
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

    private void uploadGalleryItem() {
        String caption = galleryCaption.getText().toString().trim();

        if (TextUtils.isEmpty(caption)) {
            galleryCaption.setError("Caption is required");
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

        Map<String, Object> galleryData = new HashMap<>();
        galleryData.put("caption", caption);
        galleryData.put("imageBase64", base64Image);

        databaseReference.push().setValue(galleryData)
                .addOnSuccessListener(aVoid -> {
                    uploadProgress.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(this, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show();
                    galleryCaption.setText("");
                    galleryImagePreview.setImageResource(0);
                    selectedImageUri = null;
                })
                .addOnFailureListener(e -> {
                    uploadProgress.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteGalleryItem(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Photo")
                .setMessage("Are you sure you want to delete this photo?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    String key = galleryKeys.get(position);
                    databaseReference.child(key).removeValue()
                            .addOnSuccessListener(
                                    aVoid -> Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(
                                    e -> Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadGallery() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                galleryList.clear();
                galleryKeys.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, String> item = new HashMap<>();
                    item.put("caption", ds.child("caption").getValue(String.class));
                    item.put("imageBase64", ds.child("imageBase64").getValue(String.class));
                    galleryList.add(item);
                    galleryKeys.add(ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminGalleryActivity.this, "Failed to load gallery", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_gallery, parent, false);
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
            holder.deleteBtn.setOnClickListener(v -> deleteGalleryItem(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            return galleryList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image, deleteBtn;
            TextView caption;

            ViewHolder(View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.galleryImage);
                caption = itemView.findViewById(R.id.galleryCaption);
                deleteBtn = itemView.findViewById(R.id.btnDeleteGallery);
            }
        }
    }
}
