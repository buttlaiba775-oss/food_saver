package com.example.food_saver;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class forgot_password_screen extends AppCompatActivity {

    private EditText etResetEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_screen);


        mAuth = FirebaseAuth.getInstance();

        etResetEmail = findViewById(R.id.etForgetEmail);
        Button btnSendLink = findViewById(R.id.btnSendResetLink);

        btnSendLink.setOnClickListener(v -> handlePasswordReset());
    }

    private void handlePasswordReset() {
        String email = etResetEmail.getText().toString().trim();

        // Validation Checks
        if (TextUtils.isEmpty(email)) {
            etResetEmail.setError("Email is required to receive reset link");
            etResetEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etResetEmail.setError("Please enter a valid email address");
            etResetEmail.requestFocus();
            return;
        }

        // Real Firebase Backend Flow (No Dummy Checking Here)
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(forgot_password_screen.this, "Official reset link sent! Please check your email inbox.", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email";
                        Toast.makeText(forgot_password_screen.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}