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

public class recipient_signup extends AppCompatActivity {

    private EditText etRecipientName, etRecipientFamilyMembers, etRecipientCnic, etRecipientEmail, etRecipientPhone, etRecipientAddress, etRecipientPassword, etRecipientConfirmPassword;
    private Button btnRecipientRegister;
    private TextView tvRecipientLoginLink;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipient_signup);

        mAuth = FirebaseAuth.getInstance();

        etRecipientName = findViewById(R.id.etRecipientName);
        etRecipientFamilyMembers = findViewById(R.id.etRecipientFamilyMembers);
        etRecipientCnic = findViewById(R.id.etRecipientCnic);
        etRecipientEmail = findViewById(R.id.etRecipientEmail);
        etRecipientPhone = findViewById(R.id.etRecipientPhone);
        etRecipientAddress = findViewById(R.id.etRecipientAddress);
        etRecipientPassword = findViewById(R.id.etRecipientPassword);
        etRecipientConfirmPassword = findViewById(R.id.etRecipientConfirmPassword);
        btnRecipientRegister = findViewById(R.id.btnRecipientRegister);
        tvRecipientLoginLink = findViewById(R.id.tvRecipientLoginLink);

        btnRecipientRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etRecipientName.getText().toString().trim();
                final String familyMembers = etRecipientFamilyMembers.getText().toString().trim();
                final String cnic = etRecipientCnic.getText().toString().trim();
                final String email = etRecipientEmail.getText().toString().trim();
                final String phone = etRecipientPhone.getText().toString().trim();
                final String address = etRecipientAddress.getText().toString().trim();
                String password = etRecipientPassword.getText().toString().trim();
                String confirmPassword = etRecipientConfirmPassword.getText().toString().trim();


                if (name.isEmpty() || familyMembers.isEmpty() || cnic.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(recipient_signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (cnic.length() != 13) {
                    etRecipientCnic.setError("CNIC must be exactly 13 digits (without dashes)!");
                    etRecipientCnic.requestFocus();
                    return;
                }

                // 3. Email Validation
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etRecipientEmail.setError("Please enter a valid email!");
                    etRecipientEmail.requestFocus();
                    return;
                }


                if (phone.length() != 11 || !phone.startsWith("03")) {
                    etRecipientPhone.setError("Enter a valid 11-digit phone number");
                    etRecipientPhone.requestFocus();
                    return;
                }


                if (password.length() < 6) {
                    etRecipientPassword.setError("Password must be at least 6 characters!");
                    etRecipientPassword.requestFocus();
                    return;
                }


                if (!password.equals(confirmPassword)) {
                    Toast.makeText(recipient_signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
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
                                    userMap.put("familyMembers", familyMembers);
                                    userMap.put("cnic", cnic);
                                    userMap.put("email", email);
                                    userMap.put("phone", phone);
                                    userMap.put("address", address);
                                    userMap.put("role", "Recipient");

                                    FirebaseDatabase.getInstance().getReference("Users").child(userId)
                                            .setValue(userMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(recipient_signup.this, "Recipient Registered Successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(recipient_signup.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(recipient_signup.this, "Auth Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        if (tvRecipientLoginLink != null) {
            tvRecipientLoginLink.setOnClickListener(v -> {
                Intent intent = new Intent(recipient_signup.this, login_screen.class);
                startActivity(intent);
                finish();
            });
        }
    }
}