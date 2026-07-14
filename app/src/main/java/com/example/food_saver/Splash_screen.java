package com.example.food_saver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;

public class Splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        View dot1 = findViewById(R.id.dot1);
        View dot2 = findViewById(R.id.dot2);
        View dot3 = findViewById(R.id.dot3);

        Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.blink_dot);
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.blink_dot);
        Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.blink_dot);

        dot1.startAnimation(anim1);

        new Handler().postDelayed(() -> dot2.startAnimation(anim2), 150);
        new Handler().postDelayed(() -> dot3.startAnimation(anim3), 300);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash_screen.this, login_screen.class);
            startActivity(intent);
            finish();
        }, 5000);
    }
}