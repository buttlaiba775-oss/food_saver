package com.example.food_saver;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Ngo_signup extends AppCompatActivity {

    private EditText etNgoName, etNgoRegNo, etNgoEmail, etNgoPhone, etNgoAddress, etNgoPassword, etNgoConfirmPassword;
    private Button btnNgoSignUp; //
    private TextView tvAlreadyHaveAccount; //
    private FirebaseAuth mAuth;


    private final boolean useDummyMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ngo_signup);

        mAuth = FirebaseAuth.getInstance();


        etNgoName = findViewById(R.id.etNgoName);
        etNgoRegNo = findViewById(R.id.etNgoRegNo);
        etNgoEmail = findViewById(R.id.etNgoEmail);
        etNgoPhone = findViewById(R.id.etNgoPhone);
        etNgoAddress = findViewById(R.id.etNgoAddress);
        etNgoPassword = findViewById(R.id.etNgoPassword);
        etNgoConfirmPassword = findViewById(R.id.etNgoConfirmPassword);
        btnNgoSignUp = findViewById(R.id.btnNgoRegister);
        tvAlreadyHaveAccount = findViewById(R.id.tvNgoLoginLink);

        btnNgoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etNgoName.getText().toString().trim();
                final String regNo = etNgoRegNo.getText().toString().trim();
                final String email = etNgoEmail.getText().toString().trim();
                final String phone = etNgoPhone.getText().toString().trim();
                final String address = etNgoAddress.getText().toString().trim();
                String password = etNgoPassword.getText().toString().trim();
                String confirmPassword = etNgoConfirmPassword.getText().toString().trim();


                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(regNo) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(phone) || TextUtils.isEmpty(address) ||
                        TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(Ngo_signup.this, "Field must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Ngo_signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(Ngo_signup.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (useDummyMode) {
                    Toast.makeText(Ngo_signup.this, " Registered Successfully (Dummy Mode)", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Ngo_signup.this, MainActivity.class));
                    finish();
                }

                else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                        String userId = mAuth.getCurrentUser().getUid();

                                        // Realtime Database data structure map
                                        HashMap<String, Object> ngoMap = new HashMap<>();
                                        ngoMap.put("name", name);
                                        ngoMap.put("registrationNumber", regNo);
                                        ngoMap.put("email", email);
                                        ngoMap.put("phone", phone);
                                        ngoMap.put("address", address);
                                        ngoMap.put("role", "NGO"); //
                                        ngoMap.put("status", "pending");

                                        // Saving to "Users" node
                                        FirebaseDatabase.getInstance().getReference("Users").child(userId)
                                                .setValue(ngoMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(Ngo_signup.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(Ngo_signup.this, MainActivity.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(Ngo_signup.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(Ngo_signup.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent(Ngo_signup.this, login_screen.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
