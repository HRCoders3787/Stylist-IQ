package com.example.stylistiq.Schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.stylistiq.Models.ScheduleModel;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Schedule_Clothes extends AppCompatActivity {

    TextView categoryTxt, color_tv, bottom_color_tv, date_added_tv, labels_tv;
    View clothColor, bottom_clothColor;
    ImageView topCloth, bottomCloth;
    FirebaseDatabase database;
    DatabaseReference reference;
    String _phone;
    SessionManager sessionManager;
    HashMap<String, String> userDetails;

    ArrayList<ScheduleModel> scheduleData;
    ImageButton back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_clothes);

        initialiseViews();
        
    }

    private void initialiseViews() {
        categoryTxt = findViewById(R.id.categoryTxt);
        color_tv = findViewById(R.id.color_tv);
        bottom_color_tv = findViewById(R.id.bottom_color_tv);
        date_added_tv = findViewById(R.id.date_added_tv);
        labels_tv = findViewById(R.id.labels_tv);
        clothColor = findViewById(R.id.clothColor);
        bottom_clothColor = findViewById(R.id.bottom_clothColor);
        topCloth = findViewById(R.id.topCloth);
        bottomCloth = findViewById(R.id.bottomCloth);
        back_btn = findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("Schedule").child(_phone).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    finish();
                                }
                            }
                        });

            }
        });


        sessionManager = new SessionManager(getApplicationContext(), "userLoginSession");
        userDetails = sessionManager.getUserDetailsFromSession();
        _phone = userDetails.get(SessionManager.KEY_PHONENUMBER);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();


        scheduleData = new ArrayList<>();
        extractData();
    }


    private void extractData() {
        reference.child("Schedule").child(_phone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ScheduleModel scheduleModel = dataSnapshot.getValue(ScheduleModel.class);
                                scheduleData.add(scheduleModel);
                            }
                            Glide.with(getApplicationContext()).load(scheduleData.get(0).getTopImage()).into(topCloth);
                            Glide.with(getApplicationContext()).load(scheduleData.get(0).getBottomImage()).into(bottomCloth);
                            categoryTxt.setText(scheduleData.get(0).getOccasion().toString());
                            clothColor.setBackgroundColor(scheduleData.get(0).getTopClothColor());
                            bottom_clothColor.setBackgroundColor(scheduleData.get(0).getBottomClothColor());
                            date_added_tv.setText(scheduleData.get(0).getScheduleDate());
                            labels_tv.setText(scheduleData.get(0).getSelectionFrom());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}