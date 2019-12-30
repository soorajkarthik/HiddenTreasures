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
    private int id;

    //Default Constructor
    public Treasure() {

    }

    public Treasure(double latitude, double longitude, Rarity rarity, int id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.rarity = rarity;
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getId() {
        return id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public void setId(int id) {
        this.id = id;
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
