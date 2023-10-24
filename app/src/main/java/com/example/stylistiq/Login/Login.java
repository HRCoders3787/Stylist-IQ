package com.example.stylistiq.Login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.stylistiq.DashBoard.DashBoard;
import com.example.stylistiq.Dialogs.LoadingAlert;

import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.example.stylistiq.Signup.SignUp_1;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    ImageButton backBtn;
    String phoneStr, passwordStr;
    MaterialButton loginBtn, googleLoginBtn, signupBtn;
    CheckBox rememberMe;
    EditText phoneNo, password;
    LoadingAlert loadingAlert = new LoadingAlert(Login.this);
    Pattern p = Pattern.compile("^[789]\\d{9}$");
    FirebaseFirestore db;

    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialiseViews();

        db = FirebaseFirestore.getInstance();

//        Hooks
        backBtn = findViewById(R.id.backBtn);

        loginBtn.setOnClickListener(v -> {
            loadingAlert.startAlertDialog();
            if (validateInput()) {
                phoneNo.setError(null);
                password.setError(null);
                phoneStr = phoneNo.getText().toString();
                passwordStr = password.getText().toString();

                if (rememberMe.isChecked()) { //Check if remember me clicked or not
                    sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);
                    sessionManager.createRememberMeSession(phoneStr, passwordStr);
                }

                db.collection("Users").document(phoneStr).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                if (passwordStr.equals(documentSnapshot.getString("password"))) {

                                    //Setting values to session manager
                                    SessionManager sessionManager = new SessionManager(Login.this,SessionManager.SESSION_USERSESSION);
                                    String _fullName = documentSnapshot.getString("fullName");
                                    String _emailId = documentSnapshot.getString("email");
                                    String _gender = documentSnapshot.getString("gender");
                                    sessionManager.createLoginSession(_fullName, _emailId, phoneStr, _gender, passwordStr);

                                    loadingAlert.closeAlertDialog();

                                    //Calling new Activity
                                    startActivity(new Intent(Login.this, DashBoard.class));
                                    finish();
                                } else {
                                    loadingAlert.closeAlertDialog();
                                    Toast.makeText(Login.this, "Password Wrongly mentioned", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                loadingAlert.closeAlertDialog();
                                Toast.makeText(Login.this, "No such user exists", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                loadingAlert.closeAlertDialog();
                Toast.makeText(this, "Invalid input provided", Toast.LENGTH_SHORT).show();
            }
        });

        googleLoginBtn.setOnClickListener(v -> {

        });

        backBtn.setOnClickListener(v -> finish());

        signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, SignUp_1.class));
            finish();
        });
    }

    public void initialiseViews() {
        phoneNo = findViewById(R.id.phoneNo);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        rememberMe = findViewById(R.id.rememberMe);
        sessionManager = new SessionManager(Login.this, SessionManager.SESSION_REMEMBERME);

        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailsFromSession();
            phoneNo.setText(rememberMeDetails.get(sessionManager.KEY_SESSION_PHONENUMBER));
            password.setText(rememberMeDetails.get(sessionManager.KEY_SESSION_PASSWORD));
        }
    }

    public boolean validateInput() {
        String phone = null;
        if (isEmpty()) {
            phone = phoneNo.getText().toString();
            if (!p.matcher(phone).matches()) {
                phoneNo.setError("Invalid phone number");
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean isEmpty() {
        if (phoneNo.getText().toString().isEmpty()) {
            phoneNo.setError("Phone no can't be empty");
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            password.setError("Password can't be empty");
            return false;
        }
        return true;
    }
}