package com.example.stylistiq.DashBoard.ui.suggestion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.stylistiq.Adapters.GridAdapter.SuggestionAdapter;
import com.example.stylistiq.DashBoard.ui.closet.WardrobeOutfitSuggestions;
import com.example.stylistiq.Models.SuggestionModel;
import com.example.stylistiq.R;
import com.example.stylistiq.Session.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class OutfitSuggestions extends AppCompatActivity {

    ImageButton back_btn;
    GridView gridView;
    ArrayList<SuggestionModel> suggestionData;
    SuggestionAdapter suggestionAdapter;
    FirebaseDatabase database;
    DatabaseReference reference;
    HashMap<String, String> userDetails;
    String _phone;
    SessionManager sessionManager;

    String[] parseData = new String[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outfit_suggestions);


        gridView = findViewById(R.id.gridView);
        suggestionData = new ArrayList<>();
        back_btn = findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sessionManager = new SessionManager(getApplicationContext(), "userLoginSession");
        userDetails = sessionManager.getUserDetailsFromSession();
        _phone = userDetails.get(SessionManager.KEY_PHONENUMBER);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        getSuggestionData();

        suggestionAdapter = new SuggestionAdapter(getApplicationContext(), suggestionData);
        gridView.setAdapter(suggestionAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(OutfitSuggestions.this, "POSITION : " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), WardrobeOutfitSuggestions.class);
                parseData[0] = suggestionData.get(position).getTopImg();
                parseData[1] = suggestionData.get(position).getBottomImg();
                parseData[2] = suggestionData.get(position).getSuggestionDate();
                parseData[3] = String.valueOf(suggestionData.get(position).getTopColor());
                parseData[4] = String.valueOf(suggestionData.get(position).getBottomColor());
                intent.putExtra("SuggestionData", parseData);
                startActivity(intent);
            }
        });

    }

    private void getSuggestionData() {
        reference.child("Suggestion").child(_phone)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            suggestionData.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                SuggestionModel suggestionModel = dataSnapshot.getValue(SuggestionModel.class);
                                suggestionData.add(suggestionModel);
                            }
                            suggestionAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}