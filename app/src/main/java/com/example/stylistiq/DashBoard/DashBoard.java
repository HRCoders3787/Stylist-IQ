package com.example.stylistiq.DashBoard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stylistiq.DashBoard.ui.closet.Closet;
import com.example.stylistiq.DashBoard.ui.home.Home;
import com.example.stylistiq.DashBoard.ui.weather.Weather;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DashBoard extends AppCompatActivity {

    SessionManager sessionManager;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dash_board);

        initializeViews();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Home()).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Home()).commit();
                    return true;
                } else if (item.getItemId() == R.id.weather) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Weather()).commit();
                    return true;
                } else if (item.getItemId() == R.id.closet) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Closet()).commit();
                    return true;
                }
                return false;
            }
        });


    }


    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }
}