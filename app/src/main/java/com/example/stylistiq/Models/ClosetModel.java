package com.example.stylistiq.Models;

public class ClosetModel {
    String fullName;
    String phone;


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public ClosetModel(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;

    }
}
