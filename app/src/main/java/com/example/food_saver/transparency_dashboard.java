package com.example.food_saver;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class transparency_dashboard extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTotalCollections, tvTotalDistributions, tvActiveAssignments, tvRegisteredRecipients;
    private DatabaseReference dbRefPosts, dbRefUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transparency_dashboard);

        btnBack = findViewById(R.id.btnBack);
        tvTotalCollections = findViewById(R.id.tvTotalCollections);
        tvTotalDistributions = findViewById(R.id.tvTotalDistributions);
        tvActiveAssignments = findViewById(R.id.tvActiveAssignments);
        tvRegisteredRecipients = findViewById(R.id.tvRegisteredRecipients);

        btnBack.setOnClickListener(v -> finish());

        dbRefPosts = FirebaseDatabase.getInstance().getReference("FoodPosts");
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        loadStatistics();
    }

    private void loadStatistics() {
        dbRefPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalCollections = 0;
                int totalDistributions = 0;
                int activeAssignments = 0;

                if (snapshot.exists()) {
                    totalCollections = (int) snapshot.getChildrenCount();

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String status = postSnapshot.child("status").getValue(String.class);
                        if (status != null) {
                            if (status.equalsIgnoreCase("completed")) {
                                totalDistributions++;
                            } else if (status.equalsIgnoreCase("accepted") || status.equalsIgnoreCase("pending")) {
                                activeAssignments++;
                            }
                        }
                    }
                }

                tvTotalCollections.setText(String.valueOf(totalCollections));
                tvTotalDistributions.setText(String.valueOf(totalDistributions));
                tvActiveAssignments.setText(String.valueOf(activeAssignments));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(transparency_dashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dbRefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int recipientCount = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String role = userSnapshot.child("role").getValue(String.class);
                    if (role != null && (role.equalsIgnoreCase("Recipient") || role.equalsIgnoreCase("NGO"))) {
                        recipientCount++;
                    }
                }
                tvRegisteredRecipients.setText(String.valueOf(recipientCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}