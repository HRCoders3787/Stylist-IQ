package com.example.stylistiq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

public class MultipleOutfitSuggestion extends AppCompatActivity {

    ImageView top_img, bottom_img;
    TextView color_tv, date_added_tv;
    View clothColor;
    ImageButton back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_outfit_suggestion);

        Intent intent = getIntent();
        String[] passedData = intent.getStringArrayExtra("SuggestionData");

        top_img = findViewById(R.id.top_img);
        bottom_img = findViewById(R.id.bottom_img);
        color_tv = findViewById(R.id.color_tv);
        clothColor = findViewById(R.id.clothColor);
        back_btn = findViewById(R.id.back_btn);
        date_added_tv = findViewById(R.id.date_added_tv);

        back_btn.setOnClickListener(v -> finish());
        if (passedData.length > 0) {
            Glide.with(getApplicationContext()).load(passedData[0]).into(top_img);
            Glide.with(getApplicationContext()).load(passedData[1]).into(bottom_img);
            color_tv.setText(getColorName(Integer.valueOf(passedData[3])) + " | " + getColorName(Integer.valueOf(passedData[4])));
            clothColor.setBackgroundColor(Color.parseColor(getColorName(Integer.valueOf(passedData[3]))));
            date_added_tv.setText(passedData[5]);
        } else {
            Toast.makeText(this, "Empty passed data", Toast.LENGTH_SHORT).show();
        }

    }

    private String getColorName(int colorInt) {
        HashMap<String, Integer> colorMap = new HashMap<>();
        colorMap.put("RED", Color.RED);
        colorMap.put("GREEN", Color.GREEN);
        colorMap.put("BLUE", Color.BLUE);
        colorMap.put("YELLOW", Color.YELLOW);
        colorMap.put("CYAN", Color.CYAN);
        colorMap.put("MAGENTA", Color.MAGENTA);
        colorMap.put("BLACK", Color.BLACK);
        colorMap.put("WHITE", Color.WHITE);

        String colorName = null;

        for (Map.Entry<String, Integer> entry : colorMap.entrySet()) {
            if (entry.getValue() == colorInt) {
                colorName = entry.getKey();
                break;
            }
        }

        if (colorName == null) {
            int alpha = Color.alpha(colorInt);
            int red = Color.red(colorInt);
            int green = Color.green(colorInt);
            int blue = Color.blue(colorInt);

            String hexColor = String.format("#%02x%02x%02x", red, green, blue);

            colorName = hexColor;
        }

        return colorName;
    }
}