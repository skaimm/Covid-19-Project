package com.covid.covidapp.models;

public class CityRisk {

    String country,city;
    int dusuk,odusuk,orta,oyuksek,yuksek,bilinmiyor;

    public CityRisk(String country, String city, int dusuk, int odusuk, int orta, int oyuksek, int yuksek, int bilinmiyor) {
        this.country = country;
        this.city = city;
        this.dusuk = dusuk;
        this.odusuk = odusuk;
        this.orta = orta;
        this.oyuksek = oyuksek;
        this.yuksek = yuksek;
        this.bilinmiyor = bilinmiyor;
    }

    public CityRisk(){

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getBilinmiyor() {
        return bilinmiyor;
    }

    public void setBilinmiyor() {
        this.bilinmiyor++;
    }

    public String getCity() {
        return city;
    }

    public void setCity() {
        this.city = city;
    }

    public int getDusuk() {
        return dusuk;
    }

    public void setDusuk() {
        this.dusuk++;
    }

    public int getOdusuk() {
        return odusuk;
    }

    public void setOdusuk() {
        this.odusuk++;
    }

    public int getOrta() {
        return orta;
    }

    public void setOrta() {
        this.orta++;
    }

    public int getOyuksek() {
        return oyuksek;
    }

    public void setOyuksek() {
        this.oyuksek++;
    }

    public int getYuksek() {
        return yuksek;
    }

    public void setYuksek() {
        this.yuksek++;
    }


    @Override
    public String toString() {
        return "CityRisk{" +
                "city='" + city + '\'' +
                ", dusuk=" + dusuk +
                ", odusuk=" + odusuk +
                ", orta=" + orta +
                ", oyuksek=" + oyuksek +
                ", yuksek=" + yuksek +
                ", bilinmiyor=" + bilinmiyor +
                '}';
    }
}
