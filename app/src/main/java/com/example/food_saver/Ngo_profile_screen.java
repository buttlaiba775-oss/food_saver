package com.example.food_saver;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Ngo_profile_screen extends AppCompatActivity {

    private ImageView btnBack, ivProfileImage, btnEditName, btnEditRegNo, btnEditEmail, btnEditContact, btnEditAddress, btnEditPassword;
    private TextView tvOrganizationName, tvProfileName, tvProfileRegNo, tvProfileEmail, tvProfileContact, tvProfileAddress, tvProfilePassword;
    private MaterialButton btnSaveChanges;
    private LinearLayout btnTransparencyDashboard, btnLogOut;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ngo_profile_screen);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize Views
        btnBack = findViewById(R.id.btnBack);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvOrganizationName = findViewById(R.id.tvOrganizationName);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileRegNo = findViewById(R.id.tvProfileRegNo);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileContact = findViewById(R.id.tvProfileContact);
        tvProfileAddress = findViewById(R.id.tvProfileAddress);
        tvProfilePassword = findViewById(R.id.tvProfilePassword);

        btnEditName = findViewById(R.id.btnEditName);
        btnEditRegNo = findViewById(R.id.btnEditRegNo);
        btnEditEmail = findViewById(R.id.btnEditEmail);
        btnEditContact = findViewById(R.id.btnEditContact);
        btnEditAddress = findViewById(R.id.btnEditAddress);
        btnEditPassword = findViewById(R.id.btnEditPassword);

        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnTransparencyDashboard = findViewById(R.id.btnTransparencyDashboard);
        btnLogOut = findViewById(R.id.btnLogOut);

        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
            fetchUserData();
        } else {
            Toast.makeText(this, "Guest Mode: No profile data found", Toast.LENGTH_SHORT).show();
            tvProfileName.setText(" Name");
            tvProfileEmail.setText("Email");
        }

        btnBack.setOnClickListener(v -> finish());

        btnTransparencyDashboard.setOnClickListener(v -> {
            startActivity(new Intent(Ngo_profile_screen.this, Ngo_Transparency_Dashboard.class));
        });

        btnLogOut.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(Ngo_profile_screen.this, login_screen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupEditListeners();

        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());
    }

    private void setupEditListeners() {
        btnEditName.setOnClickListener(v -> showEditDialog("name", tvProfileName));
        btnEditRegNo.setOnClickListener(v -> showEditDialog("registrationNumber", tvProfileRegNo));
        btnEditEmail.setOnClickListener(v -> showEditDialog("email", tvProfileEmail));
        btnEditContact.setOnClickListener(v -> showEditDialog("phone", tvProfileContact));
        btnEditAddress.setOnClickListener(v -> showEditDialog("address", tvProfileAddress));
        btnEditPassword.setOnClickListener(v -> showEditDialog("password", tvProfilePassword));
    }

    private void showEditDialog(String fieldKey, TextView targetTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit " + fieldKey);

        final EditText input = new EditText(this);
        if (fieldKey.equals("password")) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        input.setText(targetTextView.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newValue = input.getText().toString().trim();
            if (!newValue.isEmpty()) {
                targetTextView.setText(newValue);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveProfileChanges() {
        if (databaseReference == null) {
            Toast.makeText(this, "Guest mode: Changes not saved", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = tvProfileName.getText().toString();
        String regNo = tvProfileRegNo.getText().toString();
        String email = tvProfileEmail.getText().toString();
        String phone = tvProfileContact.getText().toString();
        String address = tvProfileAddress.getText().toString();
        String password = tvProfilePassword.getText().toString();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("registrationNumber", regNo);
        updates.put("email", email);
        updates.put("phone", phone);
        updates.put("address", address);
        updates.put("password", password);

        databaseReference.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Ngo_profile_screen.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Ngo_profile_screen.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    tvProfileName.setText(name);
                    tvOrganizationName.setText(name);
                    tvProfileRegNo.setText(snapshot.child("registrationNumber").getValue(String.class));
                    tvProfileEmail.setText(snapshot.child("email").getValue(String.class));
                    tvProfileAddress.setText(snapshot.child("address").getValue(String.class));
                    tvProfileContact.setText(snapshot.child("phone").getValue(String.class));
                    if (snapshot.hasChild("password")) {
                        tvProfilePassword.setText(snapshot.child("password").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}