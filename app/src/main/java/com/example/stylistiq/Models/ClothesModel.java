package com.example.stylistiq.Models;

public class ClothesModel {

    String clothID;
    String clotheImageUrl;
    int clothColour;
    String ClothType;
    String uploadDate;

    public String getClothType() {
        return ClothType;
    }

    public void setClothType(String clothType) {
        ClothType = clothType;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public int getClothColour() {
        return clothColour;
    }

    public void setClothColour(int clothColour) {
        this.clothColour = clothColour;
    }

    public String getClothID() {
        return clothID;
    }

    public void setClothID(String clothID) {
        this.clothID = clothID;
    }

    public String getClotheImageUrl() {
        return clotheImageUrl;
    }

    public void setClotheImageUrl(String clotheImageUrl) {
        this.clotheImageUrl = clotheImageUrl;
    }

    public ClothesModel(String clothID, String clotheImageUrl, int clothColour, String ClothType, String uploadDate) {
        this.clothID = clothID;
        this.clotheImageUrl = clotheImageUrl;
        this.clothColour = clothColour;
        this.uploadDate = uploadDate;
        this.ClothType = ClothType;
    }
}
