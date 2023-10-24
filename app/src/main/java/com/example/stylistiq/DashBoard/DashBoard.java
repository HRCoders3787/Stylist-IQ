package com.example.stylistiq.DashBoard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;

import java.util.HashMap;

public class DashBoard extends AppCompatActivity {

    TextView name, email, password, phone;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_board);

        initializeViews();

    }

    private void initializeViews() {
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);

        sessionManager = new SessionManager(DashBoard.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailsFromSession();
        name.setText(userDetails.get(SessionManager.KEY_FULLNAME));
        email.setText(userDetails.get(SessionManager.KEY_EMAIL));
        password.setText(userDetails.get(SessionManager.KEY_PASSWORD));
        phone.setText(userDetails.get(SessionManager.KEY_PHONENUMBER));
    }
}