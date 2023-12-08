package com.example.stylistiq.Models;

public class ClothesModel {

    String clothID;
    String clotheImageUrl;

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

    public ClothesModel(String shirtID, String clotheImageUrl) {
        this.clothID = shirtID;
        this.clotheImageUrl = clotheImageUrl;
    }
}
