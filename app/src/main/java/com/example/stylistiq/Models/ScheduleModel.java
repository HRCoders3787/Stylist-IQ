package com.example.stylistiq.Models;

public class ScheduleModel {

    String ScheduleID;
    String occasion;
    String scheduleDate;
    String topImage;
    String bottomImage;

    int topClothColor;
    int bottomClothColor;

    String selectionFrom;

    public ScheduleModel() {
    }

    public String getScheduleID() {
        return ScheduleID;
    }

    public void setScheduleID(String scheduleID) {
        ScheduleID = scheduleID;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getTopImage() {
        return topImage;
    }

    public void setTopImage(String topImage) {
        this.topImage = topImage;
    }

    public String getBottomImage() {
        return bottomImage;
    }

    public void setBottomImage(String bottomImage) {
        this.bottomImage = bottomImage;
    }

    public int getTopClothColor() {
        return topClothColor;
    }

    public void setTopClothColor(int topClothColor) {
        this.topClothColor = topClothColor;
    }

    public int getBottomClothColor() {
        return bottomClothColor;
    }

    public void setBottomClothColor(int bottomClothColor) {
        this.bottomClothColor = bottomClothColor;
    }

    public String getSelectionFrom() {
        return selectionFrom;
    }

    public void setSelectionFrom(String selectionFrom) {
        this.selectionFrom = selectionFrom;
    }

    public ScheduleModel(String scheduleID, String occasion, String scheduleDate, String topImage, String bottomImage, int topClothColor, int bottomClothColor, String selectionFrom) {
        ScheduleID = scheduleID;
        this.occasion = occasion;
        this.scheduleDate = scheduleDate;
        this.topImage = topImage;
        this.bottomImage = bottomImage;
        this.topClothColor = topClothColor;
        this.bottomClothColor = bottomClothColor;
        this.selectionFrom = selectionFrom;
    }
}
