package com.example.stylistiq.Authentication_Panel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.stylistiq.R;

public class Authentication extends AppCompatActivity {

    CardView logInBtn;
    Button googleBtn, signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        logInBtn = findViewById(R.id.logInBtn);
        googleBtn = findViewById(R.id.googleBtn);
        signupBtn = findViewById(R.id.signupBtn);

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Authentication.this, "login Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Authentication.this, "signup Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Authentication.this, "googleBtn Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}