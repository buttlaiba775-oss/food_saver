package com.example.food_saver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        ImageView dot1 = findViewById(R.id.dot1);
        ImageView dot2 = findViewById(R.id.dot2);
        ImageView dot3 = findViewById(R.id.dot3);


        if (dot1 != null) dot1.setAlpha(1.0f);
        if (dot3 != null) dot3.setAlpha(1.0f);


        if (dot2 != null) dot2.setAlpha(0.4f);


        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_anim);


        if (dot1 != null) dot1.startAnimation(pulseAnimation);
        if (dot2 != null) dot2.startAnimation(pulseAnimation);
        if (dot3 != null) dot3.startAnimation(pulseAnimation);


        new Handler().postDelayed(() -> {
            if (dot1 != null) dot1.clearAnimation();
            if (dot2 != null) dot2.clearAnimation();
            if (dot3 != null) dot3.clearAnimation();

            startActivity(new Intent(splash_screen.this, login_screen.class));
            finish();
        }, 4000);
    }
}