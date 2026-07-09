package com.example.food_saver;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_screen extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private final boolean isDummyMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignUpLink = findViewById(R.id.tvSignUpLink);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> validateAndLogin());

        tvSignUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(login_screen.this, role_selection_screen.class);
            startActivity(intent);
        });

        // Direct Intent bina try-catch ke, kyunki screen majood hai
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(login_screen.this, forgot_password_screen.class);
            startActivity(intent);
        });
    }

    private void validateAndLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (isDummyMode) {
            handleDummyLogin(email, password);
        } else {
            handleFirebaseLogin(email, password);
        }
    }

    private void handleDummyLogin(String email, String password) {
        if (email.equals("donor@gmail.com") && password.equals("123456")) {
            Toast.makeText(this, "Donor Login Successful", Toast.LENGTH_SHORT).show();
            navigateToDashboard("donor");
        } else if (email.equals("admin@gmail.com") && password.equals("123456")) {
            Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
            navigateToDashboard("admin");
        } else if (email.equals("recipient@gmail.com") && password.equals("123456")) {
            Toast.makeText(this, "Recipient Login Successful", Toast.LENGTH_SHORT).show();
            navigateToDashboard("recipient");
        } else if (email.equals("ngo@gmail.com") && password.equals("123456")) {
            Toast.makeText(this, "NGO Login Successful", Toast.LENGTH_SHORT).show();
            navigateToDashboard("ngo");
        } else {
            Toast.makeText(this, "Invalid Dummy Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFirebaseLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser() != null) {
                            String uid = mAuth.getCurrentUser().getUid();
                            checkUserRoleFromDatabase(uid);
                        }
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown Error";
                        Toast.makeText(login_screen.this, "Authentication Failed: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRoleFromDatabase(String uid) {
        mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if (role != null) {
                        Toast.makeText(login_screen.this, role.toUpperCase() + " Login Successful", Toast.LENGTH_SHORT).show();
                        navigateToDashboard(role);
                    } else {
                        Toast.makeText(login_screen.this, "Role not found in database", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(login_screen.this, "User record does not exist in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(login_screen.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToDashboard(String role) {
        String className = "";
        if (role.equalsIgnoreCase("donor")) {
            className = "com.example.food_saver.donor_dashboard_screen";
        } else if (role.equalsIgnoreCase("admin")) {
            className = "com.example.food_saver.admin_dashboard_screen";
        } else if (role.equalsIgnoreCase("recipient")) {
            className = "com.example.food_saver.recipient_dashboard_screen";
        } else if (role.equalsIgnoreCase("ngo")) {
            className = "com.example.food_saver.ngo_dashboard_screen";
        } else {
            Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Class<?> targetClass = Class.forName(className);
            Intent intent = new Intent(login_screen.this, targetClass);
            startActivity(intent);
            finish();
        } catch (ClassNotFoundException e) {
            Toast.makeText(this, role.toUpperCase() + " Dashboard Screen not created yet!", Toast.LENGTH_LONG).show();
        }
    }
}