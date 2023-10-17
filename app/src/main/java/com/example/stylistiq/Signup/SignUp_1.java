package com.example.stylistiq.Signup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.stylistiq.Authentication_Panel.Authentication;
import com.example.stylistiq.R;
import com.google.android.material.button.MaterialButton;

public class SignUp_1 extends AppCompatActivity {
    MaterialButton get_otp_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up1);

        get_otp_btn = findViewById(R.id.get_otp_btn);

        get_otp_btn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp_1.this, SignUp_2.class);
            startActivity(intent);
        });
    }
}