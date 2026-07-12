package com.example.food_saver.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.food_saver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NgoRequestDetailActivity extends AppCompatActivity {

    private TextView tvDetailName, tvDetailEmail, tvDetailPhone, tvDetailAddress, tvDetailRegNo;
    private Button btnApprove, btnReject;
    private String ngoId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo_request_detail);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailEmail = findViewById(R.id.tvDetailEmail);
        tvDetailPhone = findViewById(R.id.tvDetailPhone);
        tvDetailAddress = findViewById(R.id.tvDetailAddress);
        tvDetailRegNo = findViewById(R.id.tvDetailRegNo);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);

        ngoId = getIntent().getStringExtra("ngoId");

        if (ngoId != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(ngoId);
            loadNgoDetails();
        }

        btnApprove.setOnClickListener(v -> updateStatus("approved"));
        btnReject.setOnClickListener(v -> updateStatus("rejected"));
    }

    private void loadNgoDetails() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String regNo = snapshot.child("registrationNumber").getValue(String.class);

                    tvDetailName.setText(name);
                    tvDetailEmail.setText(email);
                    tvDetailPhone.setText(phone);
                    tvDetailAddress.setText(address);
                    tvDetailRegNo.setText(regNo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NgoRequestDetailActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatus(String newStatus) {
        userRef.child("status").setValue(newStatus)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "NGO " + newStatus, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}