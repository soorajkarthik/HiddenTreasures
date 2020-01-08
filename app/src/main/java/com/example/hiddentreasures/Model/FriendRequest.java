package com.example.hiddentreasures.Model;

public class FriendRequest {

  //Fields
  private long time;
  private String username;

  // Empty Constructor
  public FriendRequest() {
  }

  /**
   * Constructor
   *
   * @param time     Time that the FriendRequest was sent
   * @param username Username of the FriendRequest sender
   */
  public FriendRequest(long time, String username) {
    this.time = time;
    this.username = username;
  }

  //"Getter" and "Setter" methods
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
