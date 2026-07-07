package com.example.food_saver;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle Bundle_savedInstanceState) {
        super.onCreate(Bundle_savedInstanceState);
        setContentView(R.layout.splash_screen);


        LinearLayout dotsLayout = findViewById(R.id.dotsContainer);
        if (dotsLayout != null) {
            ObjectAnimator blinkAnimator = ObjectAnimator.ofFloat(dotsLayout, "alpha", 0.3f, 1.0f);
            blinkAnimator.setDuration(600); // Speed: 600ms
            blinkAnimator.setRepeatCount(ValueAnimator.INFINITE);
            blinkAnimator.setRepeatMode(ValueAnimator.REVERSE);
            blinkAnimator.start();
        }


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(splash_screen.this, login_screen.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}