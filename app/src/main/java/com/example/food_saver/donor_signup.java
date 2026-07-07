package com.example.food_saver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class donor_signup extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private TextView tvAlreadyHaveAccount;
    private FirebaseAuth mAuth;

    private final boolean useDummyMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donor_signup);


        mAuth = FirebaseAuth.getInstance();


        etName = findViewById(R.id.etDonorName);
        etEmail = findViewById(R.id.etDonorEmail);
        etPhone = findViewById(R.id.etDonorPhone);
        etAddress = findViewById(R.id.etDonorAddress);
        etPassword = findViewById(R.id.etDonorPassword);
        etConfirmPassword = findViewById(R.id.etDonorConfirmPassword);
        btnSignUp = findViewById(R.id.btnDonorSignup);


        tvAlreadyHaveAccount = findViewById(R.id.tvGoToLogin);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String phone = etPhone.getText().toString().trim();
                final String address = etAddress.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();


                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
                    Toast.makeText(donor_signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(donor_signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (useDummyMode) {
                    Toast.makeText(donor_signup.this, "Donor Registered Successfully (Dummy)", Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                        String userId = mAuth.getCurrentUser().getUid();

                                        // Realtime Database Database Map Setup
                                        HashMap<String, Object> userMap = new HashMap<>();
                                        userMap.put("name", name);
                                        userMap.put("email", email);
                                        userMap.put("phone", phone);
                                        userMap.put("address", address);
                                        userMap.put("role", "Donor");

                                        FirebaseDatabase.getInstance().getReference("Users").child(userId)
                                                .setValue(userMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(donor_signup.this, "  Registered Successfully", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            Toast.makeText(donor_signup.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(donor_signup.this, "Auth Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


        if (tvAlreadyHaveAccount != null) {
            tvAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Wapas login screen par bhejne ke liye
                    Intent intent = new Intent(donor_signup.this, login_screen.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}