package com.example.stylistiq.Authentication_Panel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.stylistiq.Login.Login;
import com.example.stylistiq.R;
import com.google.android.material.button.MaterialButton;

public class Authentication extends AppCompatActivity {

    MaterialButton loginBtn, googleLoginBtn, signupBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

//        Hooks
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Authentication.this, Login.class);
            startActivity(intent);
        });
    }
}