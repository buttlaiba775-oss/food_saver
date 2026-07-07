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

public class recipient_signup extends AppCompatActivity {

    private EditText etRecipientName, etRecipientFamilyMembers, etRecipientCnic, etRecipientEmail, etRecipientPhone, etRecipientAddress, etRecipientPassword, etRecipientConfirmPassword;
    private Button btnRecipientRegister;
    private TextView tvRecipientLoginLink;
    private FirebaseAuth mAuth;


    private final boolean useDummyMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipient_signup);

        // Firebase Initialize
        mAuth = FirebaseAuth.getInstance();

        // XML Views Layout Binding (As per your Component Tree IDs)
        etRecipientName = findViewById(R.id.etRecipientName);
        etRecipientFamilyMembers = findViewById(R.id.etRecipientFamilyMembers);
        etRecipientCnic = findViewById(R.id.etRecipientCnic);
        etRecipientEmail = findViewById(R.id.etRecipientEmail);
        etRecipientPhone = findViewById(R.id.etRecipientPhone);
        etRecipientAddress = findViewById(R.id.etRecipientAddress);
        etRecipientPassword = findViewById(R.id.etRecipientPassword);
        etRecipientConfirmPassword = findViewById(R.id.etRecipientConfirmPassword);
        btnRecipientRegister = findViewById(R.id.btnRecipientRegister);

        // Component tree ke mutabiq bottom "Login" link ki ID
        tvRecipientLoginLink = findViewById(R.id.tvRecipientLoginLink);

        // SIGN UP BUTTON CLICK LISTENERS
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

                // Validation Checks
                if (name.isEmpty() || familyMembers.isEmpty() || cnic.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
                    Toast.makeText(recipient_signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(recipient_signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (useDummyMode) {
                    Toast.makeText(recipient_signup.this, "Recipient Registered Successfully (Dummy)", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Firebase Auth Logic
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                        String userId = mAuth.getCurrentUser().getUid();

                                        // Realtime Database Database Map Setup
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
            }
        });


        if (tvRecipientLoginLink != null) {
            tvRecipientLoginLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Wapas login screen par bhejne ke liye
                    Intent intent = new Intent(recipient_signup.this, login_screen.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}