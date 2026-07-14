package com.example.food_saver;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class donor_dashboard_screen extends AppCompatActivity {
        private TextView tvDonations, tvActive, tvRequests, tvWelcomeUser, tvDonorRating;

    private ImageView ivNotification, ivBackArrow;
    private View notificationRedDot;

    private BottomNavigationView bottomNavigation;

    private LinearLayout emptyStateLayout;
    private DatabaseReference dbRefNotifications, databaseReference;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donor_dashboard_screen);


        tvDonations = findViewById(R.id.tvDonationsCount);
        tvActive = findViewById(R.id.tvActiveCount);
        tvRequests = findViewById(R.id.tvRequestsCount);
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvDonorRating = findViewById(R.id.tvDonorRating);
        ivNotification = findViewById(R.id.ivNotification);
        notificationRedDot = findViewById(R.id.notificationRedDot);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);


        ivBackArrow = findViewById(R.id.btnBack);
        if (ivBackArrow == null) {
            ivBackArrow = findViewById(R.id.btnBack);
        }


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Donors").child(currentUserId);
            dbRefNotifications = FirebaseDatabase.getInstance().getReference("Notifications").child(currentUserId);

            listenForDataChanges();
            listenForNotifications();
        }


        if (ivBackArrow != null) {
            ivBackArrow.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(donor_dashboard_screen.this, login_screen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }


        if (bottomNavigation != null) {

            bottomNavigation.setOnItemSelectedListener(item -> {

                int id = item.getItemId();

                if (id == R.id.nav_home) {

                    return true;

                } else if (id == R.id.nav_post) {

                    startActivity(new Intent(donor_dashboard_screen.this, food_post_screen.class));

                    return true;

                } else if (id == R.id.nav_requests) {

                    startActivity(new Intent(donor_dashboard_screen.this, request_screen.class));

                    return true;

                } else if (id == R.id.nav_profile) {

                    startActivity(new Intent(donor_dashboard_screen.this, ProfileScreen.class));

                    return true;

                }

                return false;

            });

        }

        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> {
                if (notificationRedDot != null) {
                    notificationRedDot.setVisibility(View.GONE);
                    Intent intent = new Intent(donor_dashboard_screen.this, NotificationScreen.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void listenForDataChanges() {
        if (databaseReference == null) return;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    Double rating = snapshot.child("rating").getValue(Double.class);

                    Long donations = snapshot.child("total_donations").getValue(Long.class);
                    Long active = snapshot.child("active_posts_count").getValue(Long.class);
                    Long requests = snapshot.child("total_requests").getValue(Long.class);

                    if (tvWelcomeUser != null && name != null) {
                        tvWelcomeUser.setText("Welcome " + name + "!");
                    }
                    if (tvDonorRating != null) {
                        if (rating != null) {
                            tvDonorRating.setText(String.format("%.1f ★", rating));
                        } else {
                            tvDonorRating.setText("0.0 ★");
                        }
                    }

                    if (tvDonations != null && donations != null) tvDonations.setText(String.valueOf(donations));
                    if (tvActive != null && active != null) tvActive.setText(String.valueOf(active));
                    if (tvRequests != null && requests != null) tvRequests.setText(String.valueOf(requests));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void listenForNotifications() {
        if (dbRefNotifications == null) return;

        dbRefNotifications.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    if (notificationRedDot != null) {
                        notificationRedDot.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (notificationRedDot != null) {
                        notificationRedDot.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}