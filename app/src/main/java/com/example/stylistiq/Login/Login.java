package com.example.stylistiq.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.stylistiq.R;

public class Login extends AppCompatActivity {

    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Hooks
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());
    }
}