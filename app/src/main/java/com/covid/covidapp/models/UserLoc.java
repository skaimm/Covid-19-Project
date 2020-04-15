package com.covid.covidapp.models;

public class UserLoc {

    private double lat;
    private double lng;
    private String country;
    private String city;
    private String risk;

    public UserLoc(){
    }

    public UserLoc(double lat, double lng, String country, String city,String risk) {
        this.lat = lat;
        this.lng = lng;
        this.country = country;
        this.city = city;
        this.risk = risk;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Override
    public String toString() {
        return "UserLoc{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", risk='" + risk + '\'' +
                '}';
    }
}
