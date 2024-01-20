package com.example.stylistiq.DashBoard.suggestionAlgo;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Algorithm {

    List<Integer> suggestions = new ArrayList<>();

    Context context;

    private static final double MAX_DISTANCE = 1000.0;

    public Algorithm(Context context) {
        this.context = context;
    }

    public List<Integer> getColorSuggestions(int inputColor, List<Integer> colorList) {

        for (int color : colorList) {
            if (getDistance(inputColor, color) <= MAX_DISTANCE) {
                this.suggestions.add(color);
            }
        }
        Collections.sort(suggestions, new Comparator<Integer>() {
            @Override
            public int compare(Integer color1, Integer color2) {
                return Double.compare(getDistance(inputColor, color1), getDistance(inputColor, color2));
            }
        });
        return suggestions;
    }

    private static double getDistance(int color1, int color2) {
        int r1 = Color.red(color1);
        int g1 = Color.green(color1);
        int b1 = Color.blue(color1);

        int r2 = Color.red(color2);
        int g2 = Color.green(color2);
        int b2 = Color.blue(color2);

        return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));

    }


}
