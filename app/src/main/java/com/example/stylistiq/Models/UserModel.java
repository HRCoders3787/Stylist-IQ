package com.example.stylistiq.Models;

public class UserModel {
    String fullName;
    String phone;
    String email;
    String password;
    String gender;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UserModel(String fullName, String phone, String email, String password, String gender) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }
}
