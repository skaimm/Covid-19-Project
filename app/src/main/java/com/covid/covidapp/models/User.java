package com.covid.covidapp.models;

public class User {

    private String userID;
    private String isim;
    private String phone;
    private String email;
    private String test;
    private String risk;
    private String gonul;


    public User(String userID, String isim, String phone, String email, String test, String risk, String gonul) {
        this.userID = userID;
        this.isim = isim;
        this.phone = phone;
        this.email = email;
        this.test = test;
        this.risk = risk;
        this.gonul = gonul;
    }

    public User(){

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getGonul() {
        return gonul;
    }

    public void setGonul(String gonul) {
        this.gonul = gonul;
    }


    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", isim='" + isim + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", test='" + test + '\'' +
                ", risk='" + risk + '\'' +
                ", gonul='" + gonul + '\'' +
                '}';
    }
}
