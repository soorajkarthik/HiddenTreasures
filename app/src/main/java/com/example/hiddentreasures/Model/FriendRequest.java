package com.example.hiddentreasures.Model;

public class FriendRequest {

  private long time;
  private String username;

  // Empty Constructor
  public FriendRequest() {
  }

  public FriendRequest(long time, String username) {
    this.time = time;
    this.username = username;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
