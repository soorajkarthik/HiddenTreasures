package com.example.hiddentreasures.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

enum Rarity {
    COMMON, UNCOMMON, RARE, ULTRA_RARE, LEGENDARY
}

public class Treasure implements ClusterItem {

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

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
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

    public String toString() {
        return "Lat: " + latitude + "  Long: " + longitude;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
