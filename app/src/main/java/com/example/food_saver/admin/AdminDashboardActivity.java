package com.example.food_saver.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.food_saver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalDonations, tvCompletedDonations, tvExpiredDonations;
    private TextView tvTotalUsers, tvActiveNgos, tvPendingNgos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalDonations = findViewById(R.id.tvTotalDonations);
        tvCompletedDonations = findViewById(R.id.tvCompletedDonations);
        tvExpiredDonations = findViewById(R.id.tvExpiredDonations);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvActiveNgos = findViewById(R.id.tvActiveNgos);
        tvPendingNgos = findViewById(R.id.tvPendingNgos);

        loadDashboardStats();
        setupNavigationButtons();
    }

    private void loadDashboardStats() {

        // Total Users, Active NGOs, Pending NGOs count
        FirebaseDatabase.getInstance().getReference("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long totalUsers = snapshot.getChildrenCount();
                        long approvedCount = 0;
                        long pendingCount = 0;

                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            String role = userSnap.child("role").getValue(String.class);
                            String status = userSnap.child("status").getValue(String.class);

                            if ("NGO".equals(role)) {
                                if ("approved".equals(status)) {
                                    approvedCount++;
                                } else if ("pending".equals(status)) {
                                    pendingCount++;
                                }
                            }
                        }
                        tvTotalUsers.setText(String.valueOf(totalUsers));
                        tvActiveNgos.setText(String.valueOf(approvedCount));
                        tvPendingNgos.setText(String.valueOf(pendingCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvTotalUsers.setText("0");
                        tvActiveNgos.setText("0");
                        tvPendingNgos.setText("0");
                    }
                });

        // Total Donations, Completed, Expired count
        FirebaseDatabase.getInstance().getReference("FoodPosts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long totalCount = snapshot.getChildrenCount();
                        long completedCount = 0;
                        long expiredCount = 0;

                        for (DataSnapshot postSnap : snapshot.getChildren()) {
                            String status = postSnap.child("status").getValue(String.class);
                            if ("completed".equals(status)) {
                                completedCount++;
                            } else if ("expired".equals(status)) {
                                expiredCount++;
                            }
                        }
                        tvTotalDonations.setText(String.valueOf(totalCount));
                        tvCompletedDonations.setText(String.valueOf(completedCount));
                        tvExpiredDonations.setText(String.valueOf(expiredCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvTotalDonations.setText("0");
                        tvCompletedDonations.setText("0");
                        tvExpiredDonations.setText("0");
                    }
                });
    }

    private void setupNavigationButtons() {
        Button btnNgoApproval = findViewById(R.id.btnNgoApproval);
        Button btnDonationMonitoring = findViewById(R.id.btnDonationMonitoring);
        Button btnNotifications = findViewById(R.id.btnNotifications);
        Button btnManageUsers = findViewById(R.id.btnManageUsers);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnNgoApproval.setOnClickListener(v ->
                startActivity(new Intent(this, NgoApprovalActivity.class)));

        btnManageUsers.setOnClickListener(v ->
                startActivity(new Intent(this, ManageUsersActivity.class)));

        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            finish();
        });
    }
}