package com.covid.covidapp.models;

import java.util.ArrayList;

public class CountryRisk {

    String country;
    int dusuk,odusuk,orta,oyuksek,yuksek,bilinmiyor;
    ArrayList<CityRisk> cities= new ArrayList<>();


    public CountryRisk(String country, int dusuk, int odusuk, int orta, int oyuksek, int yuksek, int bilinmiyor, ArrayList<CityRisk> cities) {
        this.country = country;
        this.dusuk = dusuk;
        this.odusuk = odusuk;
        this.orta = orta;
        this.oyuksek = oyuksek;
        this.yuksek = yuksek;
        this.bilinmiyor = bilinmiyor;
        this.cities = cities;
    }

    public CountryRisk(String country, int dusuk, int odusuk, int orta, int oyuksek, int yuksek,int bilinmiyor, CityRisk city) {
        this.country = country;
        this.dusuk = 0;
        this.odusuk = 0;
        this.orta = 0;
        this.oyuksek = 0;
        this.yuksek = 0;
        this.bilinmiyor = 0;
        this.dusuk += dusuk;
        this.odusuk += odusuk;
        this.orta += orta;
        this.oyuksek += oyuksek;
        this.yuksek += yuksek;
        this.bilinmiyor += bilinmiyor;
        this.cities.add(city);
    }
    public int getBilinmiyor() {
        return bilinmiyor;
    }

    public void setBilinmiyor(int bilinmiyor) {
        this.bilinmiyor += bilinmiyor;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDusuk() {
        return dusuk;
    }

    public void setDusuk(int dusuk) {
        this.dusuk += dusuk;
    }

    public int getOdusuk() {
        return odusuk;
    }

    public void setOdusuk(int odusuk) {
        this.odusuk += odusuk;
    }

    public int getOrta() {
        return orta;
    }

    public void setOrta(int orta) {
        this.orta += orta;
    }

    public int getOyuksek() {
        return oyuksek;
    }

    public void setOyuksek(int oyuksek) {
        this.oyuksek += oyuksek;
    }

    public int getYuksek() {
        return yuksek;
    }

    public void setYuksek(int yuksek) {
        this.yuksek += yuksek;
    }

    public ArrayList<CityRisk> getCities() {
        return cities;
    }

    public void setCities(CityRisk cities) {
        this.cities.add(cities);
    }

    public void addCities(CityRisk cityRisk) {
     this.cities.add(cityRisk);
    }

    @Override
    public String toString() {
        return "CountryRisk{" +
                "country='" + country + '\'' +
                ", dusuk=" + dusuk +
                ", odusuk=" + odusuk +
                ", orta=" + orta +
                ", oyuksek=" + oyuksek +
                ", yuksek=" + yuksek +
                ", bilinmiyor=" + bilinmiyor +
                ", cities=" + cities +
                '}';
    }
}
