package com.example.hiddentreasures.Model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Treasure implements ClusterItem {

  //Fields
  public static final String COMMON = "COMMON";
  public static final String UNCOMMON = "UNCOMMON";
  public static final String RARE = "RARE";
  public static final String ULTRA_RARE = "ULTRA_RARE";
  public static final String LEGENDARY = "LEGENDARY";
  private double latitude;
  private double longitude;
  private String rarity;
  private int id;

  // Default Constructor
  public Treasure() {
  }

  /**
   * Constructor
   *
   * @param latitude  Latitude of the treasure's position
   * @param longitude Longitude of the treasure's position
   * @param rarity    Rarity of the treasure (Common, Uncommon, Rare, Ultra-Rare, Legendary)
   * @param id        Unique ID of the treasure
   */
  public Treasure(double latitude, double longitude, String rarity, int id) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.rarity = rarity;
    this.id = id;
  }

  //"Getter" and "Setter" methods
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

  public String getRarity() {
    return rarity;
  }

  public void setRarity(String rarity) {
    this.rarity = rarity;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  //Required methods to implement ClusterItem
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
