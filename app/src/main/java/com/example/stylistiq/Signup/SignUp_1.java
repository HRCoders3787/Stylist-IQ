package com.example.stylistiq.Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.stylistiq.Dialogs.LoadingAlert;
import com.example.stylistiq.Login.Login;
import com.example.stylistiq.R;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp_1 extends AppCompatActivity {

    ImageButton backBtn;
    MaterialButton get_otp_btn, loginBtn;
    CountryCodePicker countryCode;
    LinearLayout otpLayout;
    EditText phoneNo, otpVal;

    String mobileNo;
    public static int flag = 0;
    public boolean isAlreadyVal = true;
    LoadingAlert loadingAlert = new LoadingAlert(SignUp_1.this);
    private CountDownTimer countDownTimer;
    //    private long timeLeftInMilliseconds = 10;
    private boolean timeRunning;
    MaterialButton resend_otp_btn;
    public static String verificationID = null;
    Pattern p = Pattern.compile("^[789]\\d{9}$");
    Matcher m;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up1);

        initialiseViews(); //Initialising the views of activity

        db = FirebaseFirestore.getInstance();

        get_otp_btn.setOnClickListener(v -> {
            loadingAlert.startAlertDialog();
            if (phoneNo.getText().toString().length() != 0) {
                mobileNo = phoneNo.getText().toString();
                m = p.matcher(mobileNo);
                if (m.matches()) {
                    if (isAlready(mobileNo)) {
                        if (flag == 0) {
                            sendCode(mobileNo);  //Method to send Otp Code to user
                        }

                        if (otpVal.getText().toString().trim().isEmpty() && flag == 1) {
                            loadingAlert.closeAlertDialog();
                            otpVal.setFocusable(true);
                            otpVal.setError("Otp can't be empty");
                        } else if (flag == 1) {
                            loadingAlert.startAlertDialog();
                            String code = otpVal.getText().toString().trim();
                            if (verificationID != null) {
                                phoneCredential(verificationID, code);
                            }
                        }
                    } else {
                        loadingAlert.closeAlertDialog();
                        Toast.makeText(this, "Already registered user", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    loadingAlert.closeAlertDialog();
                    phoneNo.setFocusable(true);
                    phoneNo.setError("Invalid Phone number");

                }
            } else {
                loadingAlert.closeAlertDialog();
                phoneNo.setFocusable(true);
                phoneNo.setError("Field can't empty");
            }
        });

        backBtn.setOnClickListener(v -> {
            finish();
        });

        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignUp_1.this, Login.class));
            finish();
        });
    }

    private boolean isAlready(String mobileNo) {
        db.collection("Users").document(mobileNo).get().
                addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isAlreadyVal = false;
                    } else {
                        isAlreadyVal = true;
                    }
                });
        return isAlreadyVal;
    }

    private void phoneCredential(String ID, String code) {

        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                ID,
                code
        );
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(task -> {
                    get_otp_btn.setText(R.string.next);
                    if (task.isSuccessful()) {
                        loadingAlert.closeAlertDialog();
                        Intent newIntent = new Intent(getApplicationContext(), SignUp_2.class);
                        newIntent.putExtra("intentKey", "Signup1");
                        newIntent.putExtra("phone", phoneNo.getText().toString());
                        startActivity(newIntent);
                    } else {
                        loadingAlert.closeAlertDialog();
                        Toast.makeText(this, "Otp Doesn't match", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void initialiseViews() {
        get_otp_btn = (MaterialButton) findViewById(R.id.get_otp_btn);
        phoneNo = findViewById(R.id.phoneNo);
        otpVal = findViewById(R.id.otpVal);
        countryCode = findViewById(R.id.countryCode);
        otpLayout = findViewById(R.id.otpLayout);
        backBtn = findViewById(R.id.backBtn);
        loginBtn = findViewById(R.id.loginBtn);
        resend_otp_btn = findViewById(R.id.resend_otp_btn);
    }

    public void startStopTimer() {
        if (timeRunning) {
            stopTimer();
        } else {
            startTimer();
        }
    }

    private void stopTimer() {
        countDownTimer.cancel();
        timeRunning = false;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(40 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                resend_otp_btn.setText("00:" + String.valueOf(secondsLeft) + "sec");
            }

            @Override
            public void onFinish() {
                resend_otp_btn.setText("Resend OTP");
                resend_otp_btn.setOnClickListener(v -> resendOtp());

            }
        }.start();
        timeRunning = true;
    }

    private void resendOtp() {
        loadingAlert.startAlertDialog();
        sendCode(mobileNo);
    }


    public void sendCode(String mobileNo) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobileNo,
                40,
                TimeUnit.SECONDS,
                SignUp_1.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        get_otp_btn.setText(R.string.next);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        stopTimer();
                        flag = 0;
                        Toast.makeText(SignUp_1.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        otpLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        startStopTimer();
                        verificationID = String.valueOf(verificationId);
                        flag = 1;
                        get_otp_btn.setText(R.string.next);
                        otpLayout.setVisibility(View.VISIBLE);
                        loadingAlert.closeAlertDialog();
                    }
                }
        );
    }
}