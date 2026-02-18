package com.example.application;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

public class admin_donation_activity extends AppCompatActivity {

    ListView donationlistview;
    TextView totaldonationamount;

    List<Map<String, String>> donationlist = new ArrayList<>();
    DatabaseReference donationref;
    SimpleAdapter adapter;

    int totalamount = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_donation);

        donationlistview = findViewById(R.id.donationlistview);
        totaldonationamount = findViewById(R.id.donationamount);

        donationref = FirebaseDatabase.getInstance().getReference("Donations");

        adapter = new SimpleAdapter(
                this,
                donationlist,
                R.layout.donation_list,
                new String[] { "Name", "Date", "Amount" },
                new int[] { R.id.nametext, R.id.datetext, R.id.amounttext });
        donationlistview.setAdapter(adapter);

        fetchdonation();
    }

    private void fetchdonation() {
        donationref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donationlist.clear();
                totalamount = 0;

                for (DataSnapshot donationSnap : snapshot.getChildren()) {
                    String name = donationSnap.child("Name").getValue(String.class);
                    String date = donationSnap.child("Date").getValue(String.class);
                    Object amountObj = donationSnap.child("Amount").getValue();

                    if (name == null)
                        name = "Guest";
                    if (date == null)
                        date = "N/A";

                    int amount = 0;
                    if (amountObj instanceof Long) {
                        amount = ((Long) amountObj).intValue();
                    } else if (amountObj instanceof Integer) {
                        amount = (Integer) amountObj;
                    } else if (amountObj instanceof Double) {
                        amount = (int) Math.round((Double) amountObj);
                    }

                    totalamount += amount;

                    Map<String, String> map = new HashMap<>();
                    map.put("Name", name);
                    map.put("Date", date);
                    map.put("Amount", "₹" + amount);
                    donationlist.add(map);
                }

                totaldonationamount.setText("Total Donations: ₹" + totalamount);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load donations", Toast.LENGTH_LONG).show();
            }
        });
    }
}
