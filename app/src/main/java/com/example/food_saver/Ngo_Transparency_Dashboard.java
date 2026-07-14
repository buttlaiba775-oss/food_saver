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

public class Ngo_Transparency_Dashboard extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTotalCollections, tvTotalDistributions, tvActiveAssignments, tvRegisteredRecipients;
    private DatabaseReference dbRefPosts, dbRefUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ngo_transparency_dashboard);

        // Initialize Views
        btnBack = findViewById(R.id.btnBack);
        tvTotalCollections = findViewById(R.id.tvTotalCollections);
        tvTotalDistributions = findViewById(R.id.tvTotalDistributions);
        tvActiveAssignments = findViewById(R.id.tvActiveAssignments);
        tvRegisteredRecipients = findViewById(R.id.tvRegisteredRecipients);


        btnBack.setOnClickListener(v -> finish());

        // Firebase References
        dbRefPosts = FirebaseDatabase.getInstance().getReference("FoodPosts");
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        loadDashboardData();
    }

    private void loadDashboardData() {
        // Fetch Food Posts Data
        dbRefPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int collections = 0;
                int distributions = 0;
                int active = 0;

                if (snapshot.exists()) {
                    collections = (int) snapshot.getChildrenCount();

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String status = postSnapshot.child("status").getValue(String.class);
                        if (status != null) {
                            if (status.equalsIgnoreCase("completed")) {
                                distributions++;
                            } else if (status.equalsIgnoreCase("accepted") || status.equalsIgnoreCase("pending")) {
                                active++;
                            }
                        }
                    }
                }

                tvTotalCollections.setText(String.valueOf(collections));
                tvTotalDistributions.setText(String.valueOf(distributions));
                tvActiveAssignments.setText(String.valueOf(active));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Ngo_Transparency_Dashboard.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch Registered Recipients/NGOs
        dbRefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String role = userSnapshot.child("role").getValue(String.class);
                    if (role != null && (role.equalsIgnoreCase("Recipient") || role.equalsIgnoreCase("NGO"))) {
                        count++;
                    }
                }
                tvRegisteredRecipients.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}