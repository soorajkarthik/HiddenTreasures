package com.example.hiddentreasures.Model;

enum Rarity {
    COMMON, UNCOMMMON, RARE, ULTRA_RARE, LEGENDARY
}

public class Treasure {

    private double latitude;
    private double longitude;
    private Rarity rarity;

    //Default Constructor
    public Treasure() {

    }

    public Treasure(double latitude, double longitude, Rarity rarity) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.rarity = rarity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }
}
