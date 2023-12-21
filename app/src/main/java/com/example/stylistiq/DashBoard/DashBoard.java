package com.example.stylistiq.DashBoard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.stylistiq.DashBoard.ui.closet.Closet;
import com.example.stylistiq.DashBoard.ui.home.Home;
import com.example.stylistiq.DashBoard.ui.suggestion.Suggestion;
import com.example.stylistiq.DashBoard.ui.weather.Weather;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class DashBoard extends AppCompatActivity {

    SessionManager sessionManager;

    BottomNavigationView bottomNavigationView;
    TextView toolbarHeader;


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
                    toolbarHeader.setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Home()).commit();
                    return true;
                }
                else if (item.getItemId() == R.id.weather) {
                    toolbarHeader.setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Weather()).commit();
                    return true;
                } else if (item.getItemId() == R.id.closet) {
                    toolbarHeader.setVisibility(View.VISIBLE);
                    toolbarHeader.setText("Your Closet");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new Closet()).commit();
                    return true;
                }
                return false;
            }
        });


    }


    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbarHeader = findViewById(R.id.toolbarHeader);
    }
}