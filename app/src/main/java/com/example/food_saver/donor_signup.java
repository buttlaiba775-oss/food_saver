package com.example.food_saver;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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


                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(donor_signup.this, "Field must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etEmail.setError("Please enter a valid email address!");
                    etEmail.requestFocus();
                    return;
                }


                if (phone.length() != 11 || !phone.startsWith("03")) {
                    etPhone.setError("Enter a valid 11-digit phone number (e.g., 03XXXXXXXXX)");
                    etPhone.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    etPassword.setError("Password must be at least 6 characters ");
                    etPassword.requestFocus();
                    return;
                }

                // 5. Passwords Match Validation
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(donor_signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                    String userId = mAuth.getCurrentUser().getUid();

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
                                                        Toast.makeText(donor_signup.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
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
        });

        if (tvAlreadyHaveAccount != null) {
            tvAlreadyHaveAccount.setOnClickListener(v -> {
                Intent intent = new Intent(donor_signup.this, login_screen.class);
                startActivity(intent);
                finish();
            });
        }
    }
}