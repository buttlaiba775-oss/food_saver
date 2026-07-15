package com.example.food_saver;

import android.app.Activity;
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

public class ProfileScreen extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private ImageView btnBack, ivProfileImage, btnCamera, btnEditName, btnEditEmail, btnEditContact, btnEditAddress, btnEditPassword;
    private TextView tvOrganizationName, tvProfileName, tvProfileEmail, tvProfileContact, tvProfileAddress, tvProfilePassword;
    private MaterialButton btnSaveChanges;
    private LinearLayout btnTransparencyDashboard, btnLogOut;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        btnBack = findViewById(R.id.btnBack);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        btnCamera = findViewById(R.id.btnCamera);
        tvOrganizationName = findViewById(R.id.tvOrganizationName);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileAddress = findViewById(R.id.tvProfileAddress);
        tvProfileContact = findViewById(R.id.tvProfileContact);
        btnEditName = findViewById(R.id.btnEditName);
        btnEditEmail = findViewById(R.id.btnEditEmail);
        btnEditContact= findViewById(R.id.btnEditContact);
        btnEditAddress = findViewById(R.id.btnEditAddress);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        tvProfilePassword = findViewById(R.id.tvProfilePassword);
        btnEditPassword = findViewById(R.id.btnEditPassword);
        btnTransparencyDashboard = findViewById(R.id.btnTransparencyDashboard);
        btnLogOut = findViewById(R.id.btnLogOut);

        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
            fetchUserData();
        } else {
            Toast.makeText(this, "Guest Mode: No profile data found", Toast.LENGTH_SHORT).show();
            tvProfileName.setText("Name");
            tvProfileEmail.setText("Email");
        }

        btnBack.setOnClickListener(v -> finish());

        btnCamera.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        btnTransparencyDashboard.setOnClickListener(v -> startActivity(new Intent(ProfileScreen.this, transparency_dashboard.class)));

        btnLogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileScreen.this, login_screen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupEditListeners();

        btnSaveChanges.setOnClickListener(v -> {
            saveProfileChanges();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            ivProfileImage.setPadding(0,0,0,0);
            ivProfileImage.setImageURI(data.getData());
            Toast.makeText(this, "Profile photo uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupEditListeners() {
        btnEditName.setOnClickListener(v -> showEditDialog("name", tvProfileName));
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
        String email = tvProfileEmail.getText().toString();
        String phone = tvProfileContact.getText().toString();
        String address = tvProfileAddress.getText().toString();
        String password = tvProfilePassword.getText().toString();

        java.util.HashMap<String, Object> updates = new java.util.HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("phone", phone);
        updates.put("address", address);
        updates.put("password", password);

        databaseReference.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileScreen.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileScreen.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
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
                    tvProfileEmail.setText(snapshot.child("email").getValue(String.class));
                    tvProfileAddress.setText(snapshot.child("address").getValue(String.class));
                    tvProfileContact.setText(snapshot.child("phone").getValue(String.class));
                    if (snapshot.hasChild("password")) {
                        tvProfilePassword.setText(snapshot.child("password").getValue(String.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}