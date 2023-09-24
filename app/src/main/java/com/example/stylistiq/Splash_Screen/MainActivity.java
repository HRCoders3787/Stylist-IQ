package com.example.stylistiq.Splash_Screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.example.stylistiq.Onboarding_Screen.Onboarding1;
import com.example.stylistiq.R;

public class MainActivity extends AppCompatActivity {
    LottieAnimationView lottie_animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Hooks
        lottie_animation = findViewById(R.id.lottie_animation);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, Onboarding1.class);
                startActivity(intent);
            }
        }, 3000);
    }
}