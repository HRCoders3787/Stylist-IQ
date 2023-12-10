package com.example.stylistiq.Models;

public class ClothesModel {

    String clothID;
    String clotheImageUrl;

    int clothColour;

    public int getClothColour() {
        return clothColour;
    }

    public void setClothColour(int clothColour) {
        this.clothColour = clothColour;
    }

    public String getShirtID() {
        return clothID;
    }

    public void setShirtID(String shirtID) {
        this.clothID = shirtID;
    }

    public String getClotheImageUrl() {
        return clotheImageUrl;
    }

    public void setClotheImageUrl(String clotheImageUrl) {
        this.clotheImageUrl = clotheImageUrl;
    }

    public ClothesModel(String shirtID, String clotheImageUrl, int clothColour) {
        this.clothID = shirtID;
        this.clotheImageUrl = clotheImageUrl;
        this.clothColour = clothColour;
    }
}
