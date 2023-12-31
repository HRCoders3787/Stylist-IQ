package com.example.stylistiq.Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stylistiq.DashBoard.DashBoard;
import com.example.stylistiq.Dialogs.LoadingAlert;
import com.example.stylistiq.Models.UserModel;
import com.example.stylistiq.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp_2 extends AppCompatActivity {

    EditText fullName, email, password;
    RadioGroup genderRadio;
    String phoneStr, nameStr, emailStr, passwordStr, genderStr;
    ImageButton backBtn;
    RadioButton male_rb, female_rb;
    TextView inputTextView;
    MaterialButton signupBtn;
    Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    Pattern phonePattern = Pattern.compile("^[789]\\d{9}$");
    FirebaseFirestore db;
    LoadingAlert loadingAlert = new LoadingAlert(SignUp_2.this);
    String prevIntentKey;
    Intent prevIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up2);

        prevIntent = getIntent();
        prevIntentKey = prevIntent.getStringExtra("intentKey");

        initialiseViews();
        db = FirebaseFirestore.getInstance();

        genderRadio.setOnCheckedChangeListener((group, checkedId) -> {
            if (R.id.male_rb == checkedId) {
                genderStr = male_rb.getText().toString();
            } else if (R.id.female_rb == checkedId) {
                genderStr = female_rb.getText().toString();
            }
        });
        signupBtn.setOnClickListener(v -> {
            loadingAlert.startAlertDialog();
            if (inputValidate()) {
                fullName.setError(null);
                password.setError(null);
                email.setError(null);

                nameStr = fullName.getText().toString();
                emailStr = email.getText().toString();
                passwordStr = password.getText().toString();

                db.collection("Users").document(phoneStr)
                        .set(new UserModel(nameStr, phoneStr, emailStr, passwordStr, genderStr))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                loadingAlert.closeAlertDialog();
                                startActivity(new Intent(SignUp_2.this, DashBoard.class));
                                finish();
                            } else {
                                loadingAlert.closeAlertDialog();
                                Toast.makeText(this, "Not able to add data", Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                loadingAlert.closeAlertDialog();
                Toast.makeText(SignUp_2.this, "Input's are not valid", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private boolean inputValidate() {
        if (emptyValidate()) {
            String input_email = email.getText().toString();
            if (prevIntentKey.equals("Signup1")) {
                if (!emailPattern.matcher(input_email).matches()) {
                    email.setError("Invalid email address");
                }
            } else if (password.getText().length() < 8) {
                password.setError("Length be greater or equal 8");
            } else if (prevIntentKey.equals("GoogleSignup")) {
                if (input_email.length() != 10) { //Validating length based
                    email.setError("Invalid phone number");
                } else if (!phonePattern.matcher(input_email).matches()) { //Validating based on pattern
                    email.setError("Invalid Phone number");
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean emptyValidate() {
        if (fullName.getText().toString().isEmpty()) {
            fullName.setError("Field can't empty");
        } else if (email.getText().toString().isEmpty()) {
            email.setError("Field can't be empty");
        } else if (password.getText().toString().isEmpty()) {
            password.setError("Field can't be empty");
        } else {

            return true;
        }
        return false;
    }

    private void initialiseViews() {
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn);
        genderRadio = findViewById(R.id.genderRadio);
        male_rb = findViewById(R.id.male_rb);
        female_rb = findViewById(R.id.female_rb);
        backBtn = findViewById(R.id.backBtn);
        inputTextView = findViewById(R.id.inputTextView);

        if (prevIntentKey.equals("GoogleSignup")) {
            emailStr = prevIntent.getStringExtra("email");
            inputTextView.setText("Phone number");
            email.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            email.setHint(R.string.phone_hint);
        } else {
            phoneStr = prevIntent.getStringExtra("phone");
        }
    }
}