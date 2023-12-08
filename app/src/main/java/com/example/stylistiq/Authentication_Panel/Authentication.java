package com.example.stylistiq.Authentication_Panel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.stylistiq.DashBoard.DashBoard;
import com.example.stylistiq.Login.Login;
import com.example.stylistiq.R;
import com.example.stylistiq.Signup.SignUp_1;
import com.example.stylistiq.Signup.SignUp_2;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Authentication extends AppCompatActivity {

    MaterialButton loginBtn, googleLoginBtn, signupBtn;
    FirebaseAuth auth;
    FirebaseFirestore db;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);


//        Hooks
        initializeViews();

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Authentication.this, Login.class);
            startActivity(intent);
        });


        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Authentication.this, SignUp_1.class);
            startActivity(intent);
        });

        googleLoginBtn.setOnClickListener(v -> {
            googleSignIn();
        });
    }

    public void googleSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("id", user.getUid());
                        map.put("name", user.getDisplayName());
                        map.put("profile", user.getPhotoUrl());
                        Intent newIntent = new Intent(getApplicationContext(), SignUp_2.class);
                        newIntent.putExtra("intentKey", "GoogleSignup");
                        newIntent.putExtra("email", user.getEmail());
                        startActivity(newIntent);

                    } else {
                        Toast.makeText(this, "failed to sign in", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initializeViews() {
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        googleLoginBtn = findViewById(R.id.googleLoginBtn);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Back btn pressed", Toast.LENGTH_SHORT).show();
        showExitConfirmationDialog();

    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User confirmed, exit the app
                        //finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User canceled, close the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }
}