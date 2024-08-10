package com.example.stylistiq.Weather.SuggestionAlgo;

public class TemperatureRange {
    int tempMin;
    int tempMax;
    String category;
    String[] colors;


    public TemperatureRange(int tempMin, int tempMax, String category, String[] colors) {
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.category = category;
        this.colors = colors;
    }

    // Getters and setters (if needed)
    public int getTempMin() {
        return tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public String getCategory() {
        return category;
    }

    public String[] getColors() {
        return colors;
    }
}
