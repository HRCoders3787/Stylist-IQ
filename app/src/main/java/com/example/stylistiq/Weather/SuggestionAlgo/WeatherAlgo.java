package com.example.stylistiq.Weather.SuggestionAlgo;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherAlgo {
    private static final double COLOR_SIMILARITY_THRESHOLD = 50.0;
    private List<TemperatureRange> temperatureRanges;
    Context context;
    String tempStr;
    String inputColor;

    public WeatherAlgo(Context context, String tempStr, String inputColor) {
        this.context = context;
        this.tempStr = tempStr;
        this.inputColor = inputColor;

        int hexValue = Integer.valueOf(inputColor);
        this.inputColor = getColorName(hexValue);

        try {
            temperatureRanges = parseCSV(context, "colors.csv");
        } catch (Exception e) {
            Log.e("WRONG COLORS ", e.getMessage());
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

    public boolean startSuggestion() {
        try {
            int temperature = Integer.parseInt(tempStr);
            boolean isColorValid = isColorClose(inputColor, temperature, temperatureRanges);
            if (isColorValid) {
//                txtColor.setBackgroundColor(Color.parseColor(inputColor));
                Toast.makeText(context, "The color " + inputColor + " is close to a valid color", Toast.LENGTH_SHORT).show();
                return true;
//                txtColor.setText("The color " + inputColor + " is close to a valid color for temperature " + temperature);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Invalid temperature input", Toast.LENGTH_SHORT).show();
//            txtColor.setText("Invalid temperature input");
        }
        Toast.makeText(context, "The color " + inputColor + " is NOT close to any valid color", Toast.LENGTH_SHORT).show();
        return false;
    }


    public boolean isColorClose(String inputColor, int temperature, List<TemperatureRange> ranges) {
        int[] inputRgb = hexToRgb(inputColor);

        for (TemperatureRange range : ranges) {
            if (temperature >= range.getTempMin() && temperature <= range.getTempMax()) {
                for (String color : range.getColors()) {
                    int[] rangeRgb = hexToRgb("#" + color);
                    if (colorDifference(inputRgb, rangeRgb) <= COLOR_SIMILARITY_THRESHOLD) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<TemperatureRange> parseCSV(Context context, String fileName) throws IOException {

        List<TemperatureRange> ranges = new ArrayList<>();
        AssetManager assetManager = context.getAssets();
        try (InputStream is = assetManager.open(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            // Skip header line
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int tempMin = Integer.parseInt(values[0]);
                int tempMax = Integer.parseInt(values[1]);
                String category = values[8];
                String[] colors = Arrays.copyOfRange(values, 3, values.length);
                ranges.add(new TemperatureRange(tempMin, tempMax, category, colors));
            }

        }
        return ranges;
    }

    public int[] hexToRgb(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return new int[]{r, g, b};
    }

    public double colorDifference(int[] rgb1, int[] rgb2) {
        int rDiff = rgb1[0] - rgb2[0];
        int gDiff = rgb1[1] - rgb2[1];
        int bDiff = rgb1[2] - rgb2[2];
        return Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
    }
}
