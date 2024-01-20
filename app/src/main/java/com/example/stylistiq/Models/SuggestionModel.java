package com.example.stylistiq.Models;

public class SuggestionModel {
    String suggestionID;
    int TopColor;
    int BottomColor;
    String topImg;

    String suggestionDate;
    String BottomImg;

    public SuggestionModel() {
    }

    public SuggestionModel(String suggestionID, int topColor, int bottomColor, String topImg, String bottomImg, String suggestionDate) {
        this.suggestionID = suggestionID;
        this.TopColor = topColor;
        this.BottomColor = bottomColor;
        this.topImg = topImg;
        this.BottomImg = bottomImg;
        this.suggestionDate = suggestionDate;
    }

    public String getSuggestionDate() {
        return suggestionDate;
    }

    public void setSuggestionDate(String suggestionDate) {
        this.suggestionDate = suggestionDate;
    }

    public String getSuggestionID() {
        return suggestionID;
    }

    public void setSuggestionID(String suggestionID) {
        this.suggestionID = suggestionID;
    }

    public int getTopColor() {
        return TopColor;
    }

    public void setTopColor(int topColor) {
        TopColor = topColor;
    }

    public int getBottomColor() {
        return BottomColor;
    }

    public void setBottomColor(int bottomColor) {
        BottomColor = bottomColor;
    }

    public String getTopImg() {
        return topImg;
    }

    public void setTopImg(String topImg) {
        this.topImg = topImg;
    }

    public String getBottomImg() {
        return BottomImg;
    }

    public void setBottomImg(String bottomImg) {
        this.BottomImg = bottomImg;
    }
}
