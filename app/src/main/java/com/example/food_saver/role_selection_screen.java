package com.example.food_saver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class role_selection_screen extends AppCompatActivity {


    private Button btnDonor, btnNgo, btnRecipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.role_selection_screen);


        btnDonor = findViewById(R.id.btnDonor);
        btnNgo = findViewById(R.id.btnNgo);
        btnRecipient = findViewById(R.id.btnRecipient);


        btnDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(role_selection_screen.this, donor_signup.class);
                startActivity(intent);
            }
        });


        btnNgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User ko NGO Signup screen par bhejein
                Intent intent = new Intent(role_selection_screen.this, Ngo_signup.class);
                startActivity(intent);
            }
        });


        btnRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(role_selection_screen.this, recipient_signup.class);
                startActivity(intent);
            }
        });
    }
}