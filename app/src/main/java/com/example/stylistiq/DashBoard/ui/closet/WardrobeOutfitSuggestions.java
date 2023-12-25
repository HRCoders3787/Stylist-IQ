package com.example.stylistiq.DashBoard.ui.closet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stylistiq.Models.ClothesModel;
import com.example.stylistiq.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class WardrobeOutfitSuggestions extends AppCompatActivity {


    ArrayList<ClothesModel> allClothList;
    String receivedData[] = new String[6];
    ImageButton back_btn;
    ImageView cloth_img;
    TextView categoryTxt, date_added_tv, color_tv;
    View clothColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wardrobe_outfit_suggestions);
        Intent intent = getIntent();

        initialiseViews();

        receivedData = intent.getStringArrayExtra("clothData");
        Toast.makeText(this, "RECEIVED DATA SIZE: " + receivedData.length, Toast.LENGTH_SHORT).show();
        categoryTxt.setText(receivedData[2]);
        date_added_tv.setText(receivedData[4]);
        Glide.with(WardrobeOutfitSuggestions.this).load(receivedData[1]).into(cloth_img);
        clothColor.setBackgroundColor(Integer.parseInt(receivedData[3]));
        color_tv.setText(receivedData[5]);

    }

    public void initialiseViews() {
        back_btn = findViewById(R.id.back_btn);
        categoryTxt = findViewById(R.id.categoryTxt);
        date_added_tv = findViewById(R.id.date_added_tv);
        clothColor = findViewById(R.id.clothColor);
        cloth_img = findViewById(R.id.cloth_img);
        color_tv = findViewById(R.id.color_tv);

        back_btn.setOnClickListener(v -> finish());


    }
}