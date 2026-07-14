package com.example.food_saver;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationScreen extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvNotifications;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_screen);

        btnBack = findViewById(R.id.btnBack);
        tvNotifications = findViewById(R.id.tvNotifications);

        btnBack.setOnClickListener(v -> finish());

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            dbRef = FirebaseDatabase.getInstance()
                    .getReference("Notifications")
                    .child(uid);

            loadNotifications();
        }
    }

    private void loadNotifications() {

        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    StringBuilder builder = new StringBuilder();

                    for (DataSnapshot data : snapshot.getChildren()) {

                        String message = data.child("message").getValue(String.class);

                        if (message != null) {
                            builder.append("• ").append(message).append("\n\n");
                        }
                    }

                    tvNotifications.setText(builder.toString());

                } else {

                    tvNotifications.setText("No new notifications.");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                tvNotifications.setText("Error loading notifications.");

            }
        });
    }
}