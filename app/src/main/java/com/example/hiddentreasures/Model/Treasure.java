package com.example.hiddentreasures.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Treasure implements ClusterItem {

    private double latitude;
    private double longitude;
    private String rarity;
    private int id;

    //Default Constructor
    public Treasure() {

    }

    public Treasure(double latitude, double longitude, String rarity, int id) {
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

    public String getRarity() {
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

    public void setRarity(String rarity) {
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
