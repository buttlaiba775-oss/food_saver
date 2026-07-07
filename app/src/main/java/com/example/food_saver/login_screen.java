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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_screen extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUpLink; 
    private FirebaseAuth mAuth;

    private final boolean useDummyMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        
        tvSignUpLink = findViewById(R.id.tvSignUpLink);

        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(login_screen.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (useDummyMode) {
                    if (email.equals("admin@gmail.com") && password.equals("123456")) {
                        Toast.makeText(login_screen.this, " Login successfully (Dummy)", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login_screen.this, MainActivity.class));
                        finish();
                    }
                    else if (email.equals("donor@gmail.com") && password.equals("123456")) {
                        Toast.makeText(login_screen.this, " Login successfully (Dummy)", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login_screen.this, MainActivity.class));
                        finish();
                    }
                    else if (email.equals("ngo@gmail.com") && password.equals("123456")) {
                        Toast.makeText(login_screen.this, " Login successfully (Dummy)", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login_screen.this, MainActivity.class));
                        finish();
                    }
                    else if (email.equals("recipient@gmail.com") && password.equals("123456")) {
                        Toast.makeText(login_screen.this, "Login successfully (Dummy)", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login_screen.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(login_screen.this, "Invalid Credentials (Dummy Mode)", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                        String userId = mAuth.getCurrentUser().getUid();

                                        FirebaseDatabase.getInstance().getReference("Users").child(userId)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            String role = snapshot.child("role").getValue(String.class);

                                                            if ("Admin".equals(role)) {
                                                                Toast.makeText(login_screen.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(login_screen.this, MainActivity.class));
                                                            } else if ("Donor".equals(role)) {
                                                                Toast.makeText(login_screen.this, "Welcome Donor!", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(login_screen.this, MainActivity.class));
                                                            } else if ("NGO".equals(role)) {
                                                                Toast.makeText(login_screen.this, "Welcome NGO!", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(login_screen.this, MainActivity.class));
                                                            } else if ("Recipient".equals(role)) {
                                                                Toast.makeText(login_screen.this, "Welcome Recipient!", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(login_screen.this, MainActivity.class));
                                                            }
                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(login_screen.this, "Database Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Login Failed";
                                        Toast.makeText(login_screen.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });

        
        if (tvSignUpLink != null) {
            tvSignUpLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(login_screen.this, role_selection_screen.class);
                    startActivity(intent);
                }
            });
        }
    }
}
