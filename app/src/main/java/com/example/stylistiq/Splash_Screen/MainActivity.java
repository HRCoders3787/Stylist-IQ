package com.example.stylistiq.Splash_Screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.example.stylistiq.DashBoard.DashBoard;
import com.example.stylistiq.Onboarding_Screen.Onboarding1;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    LottieAnimationView lottie_animation;
    SessionManager sessionManager;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Hooks
        lottie_animation = findViewById(R.id.lottie_animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(MainActivity.this,"userLoginSession");
                HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
                phone = userDetails.get(SessionManager.KEY_PHONENUMBER);
                String IS_login = userDetails.get(SessionManager.IS_LOGIN);
                if (phone == null){
                    Intent intent = new Intent(MainActivity.this, Onboarding1.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent dashBoard = new Intent(MainActivity.this, DashBoard.class);
                    startActivity(dashBoard);
                    finish();
                }
            }
        }, 3000);
    }
}