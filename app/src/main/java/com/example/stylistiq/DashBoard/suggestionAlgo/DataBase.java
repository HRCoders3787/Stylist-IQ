package com.example.stylistiq.DashBoard.suggestionAlgo;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.stylistiq.Models.SuggestionModel;
import com.example.stylistiq.Session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class DataBase {
    String bottomType;
    String toptype;
    FirebaseDatabase database;
    DatabaseReference reference;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    String imageClass;
    Context context;
    String _phone;
    int topColor, BottomColor;
    String topImg, bottomImg;

    public DataBase(Context context, int topColor, int BottomColor, String topImg, String bottomImg) {
        this.context = context;
        this.topColor = topColor;
        this.BottomColor = BottomColor;
        this.topImg = topImg;
        this.bottomImg = bottomImg;
//        this.imageClass = imageClass;

    }

    public boolean insertToDatabase() {
        final boolean[] flag = {false};
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        String UploadDate = getCurrentDate();
        _phone = getSessionDetails();
        String ID = createSuggestionID();
        SuggestionModel suggestionModel = new SuggestionModel(ID, topColor, BottomColor, topImg, UploadDate, bottomImg);
        reference.child("Suggestion").child(_phone)
                .child(ID)
                .setValue(suggestionModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        flag[0] = true;
                    } else {
                        Toast.makeText(context, "Not Successfully realtime updated", Toast.LENGTH_SHORT).show();
                    }
                });
        return flag[0];
    }

    private String getSessionDetails() {
        sessionManager = new SessionManager(context, "userLoginSession");
        userDetails = sessionManager.getUserDetailsFromSession();
        return userDetails.get(SessionManager.KEY_PHONENUMBER);
    }

    private String createSuggestionID() {
        String ID = "SG";
        Random random = new Random();
        int number = random.nextInt(9000) + 1000;
        return ID + number;
    }

    private String getCurrentDate() {
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd, MMM yyyy");
        }
        String date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = currentDate.format(formatter);
        }

        return date;
    }


}
