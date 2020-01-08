package com.example.hiddentreasures.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class User implements Comparable {

  //Fields
  private String email;
  private String username;
  private String password;
  private long dateJoined;
  private long lastSeen;
  private String instanceToken;
  private ArrayList<String> friendList;
  private ArrayList<FriendRequest> friendRequests;
  private ArrayList<Integer> treasuresFoundTodayIDs;
  private HashMap<String, Integer> foundTreasures;

  //Empty constructor
  public User() {

    friendList = new ArrayList<>();
    friendRequests = new ArrayList<>();
    treasuresFoundTodayIDs = new ArrayList<>();
    foundTreasures =
        new HashMap<String, Integer>() {
          {
            put(Treasure.COMMON, 0);
            put(Treasure.UNCOMMON, 0);
            put(Treasure.RARE, 0);
            put(Treasure.ULTRA_RARE, 0);
            put(Treasure.LEGENDARY, 0);
          }
        };
  }

  /**
   * Constructor
   *
   * @param email      Email address of the user
   * @param username   Username of the user
   * @param password   User's password
   * @param dateJoined Date the user registered for the app
   */
  public User(String email, String username, String password, long dateJoined) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.dateJoined = dateJoined;

    friendList = new ArrayList<>();
    friendRequests = new ArrayList<>();
    treasuresFoundTodayIDs = new ArrayList<>();
    foundTreasures =
        new HashMap<String, Integer>() {
          {
            put(Treasure.COMMON, 0);
            put(Treasure.UNCOMMON, 0);
            put(Treasure.RARE, 0);
            put(Treasure.ULTRA_RARE, 0);
            put(Treasure.LEGENDARY, 0);
          }
        };
  }

  //"Getter" and "Setter" methods
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public long getDateJoined() {
    return dateJoined;
  }

  public long getLastSeen() {
    return lastSeen;
  }

  public void setLastSeen(long lastSeen) {
    this.lastSeen = lastSeen;
  }

  public String getInstanceToken() {
    return instanceToken;
  }

  public void setInstanceToken(String token) {
    instanceToken = token;
  }

  public ArrayList<String> getFriendList() {
    return friendList;
  }

  public ArrayList<FriendRequest> getFriendRequests() {
    return friendRequests;
  }

  public ArrayList<Integer> getTreasuresFoundTodayIDs() {
    return treasuresFoundTodayIDs;
  }

  public HashMap<String, Integer> getFoundTreasures() {
    return foundTreasures;
  }

  public void setFoundTreasures(HashMap<String, Integer> foundTreasures) {
    this.foundTreasures = foundTreasures;
  }

  /**
   * @return A user to be used in the leaderboards. Has the same score and lastSeen as the current
   * user. The user's username has been replaced with "You"
   */
  public User placeHolderUser() {
    User temp = new User(null, "You", null, dateJoined);
    temp.setLastSeen(System.currentTimeMillis());
    temp.setFoundTreasures(foundTreasures);
    return temp;
  }

  /**
   * Adds friend request to the user's list of requests
   *
   * @param friendRequest FriendRequest that is to be added
   */
  public void addFriendRequest(FriendRequest friendRequest) {
    friendRequests.add(friendRequest);
  }

  /**
   * Removes friend request from the user's list of requests
   *
   * @param friendRequest FriendRequest that is to be removed
   */
  public void removeFriendRequest(FriendRequest friendRequest) {
    friendRequests.remove(friendRequest);
  }

  /**
   * Removes friend requests from user's list of requests from the specified username
   *
   * @param username Username of whose friend request should be removed
   */
  public void removeFriendRequestFromUser(String username) {
    friendRequests.removeIf(friendRequest -> friendRequest.getUsername().equals(username));
  }

  /**
   * Adds sender of given friend request to user's friend list
   *
   * @param friendRequest FriendRequest whose user must be added as a friend
   */
  public void acceptFriendRequest(FriendRequest friendRequest) {
    friendRequests.remove(friendRequest);
    addFriend(friendRequest.getUsername());
  }

  /**
   * @param username Username that is to be searched for
   * @return True if user has a friend request from the given username, false if not
   */
  public boolean hasFriendRequestFromUser(String username) {
    return friendRequests.stream()
        .anyMatch(friendRequest -> friendRequest.getUsername().equals(username));
  }

  /**
   * @param username Username that is to be searched for
   * @return True if user is a friend of the given username, false if not
   */
  public boolean isFriendOfUser(String username) {
    return friendList.contains(username);
  }

  /**
   * Adds given username to the user's friends list
   *
   * @param username Username that is to be added to friend list
   */
  public void addFriend(String username) {
    friendList.add(username);
  }

  /**
   * Removes given username from the user's friend list
   *
   * @param username Username that is to be removed from friend list
   */
  public void removeFriend(String username) {
    friendList.remove(username);
  }

  /**
   * Adds treasure's id to list of ids of treasures that were found today. Adds treasure to HashMap
   * that keeps track of all treasures that have been found
   *
   * @param treasure Treasure that is to be added
   */
  public void addFoundTreasure(Treasure treasure) {
    treasuresFoundTodayIDs.add(treasure.getId());
    foundTreasures.put(treasure.getRarity(), foundTreasures.get(treasure.getRarity()) + 1);
  }

  /**
   * @return User's score calculated based on all of the treasures they have found
   */
  public int calculateScore() {

    int score = 0;

    score += foundTreasures.get(Treasure.COMMON) * 100;
    score += foundTreasures.get(Treasure.UNCOMMON) * 250;
    score += foundTreasures.get(Treasure.RARE) * 500;
    score += foundTreasures.get(Treasure.ULTRA_RARE) * 1500;
    score += foundTreasures.get(Treasure.LEGENDARY) * 5000;

    return score;
  }

  /**
   * @return String representation of the user's score breakdown
   */
  public String scoreSummary() {
    return "Total Score:  "
        + calculateScore()
        + "\nCommon:       " + foundTreasures.get(Treasure.COMMON)
        + "\nUncommon:     " + foundTreasures.get(Treasure.UNCOMMON)
        + "\nRare:         " + foundTreasures.get(Treasure.RARE)
        + "\nUltra-Rare:   " + foundTreasures.get(Treasure.ULTRA_RARE)
        + "\nLegendary:    " + foundTreasures.get(Treasure.LEGENDARY);
  }

  /**
   * Compares the current user to the user based on their scores. If scores are equal, then the
   * users are compared based on date joined. The larger scores and the earlier join dates are
   * "greater"
   *
   * @param o User object that the current user is to be compared to
   * @return a negative number, zero, or a positive number based on if the current user is less
   * than, equal to, or greater than the given user
   */
  @Override
  public int compareTo(Object o) {
    int scoreDiff = ((User) o).calculateScore() - calculateScore();
    int timeDiff = ((User) o).getDateJoined() > getDateJoined() ? 1 : -1;

    return scoreDiff == 0 ? timeDiff : scoreDiff;
  }

  /**
   * Compares the current user to the given user based on their usernames
   *
   * @param o User object that the current user is to be compared to
   * @return True if the current user has the same username as the given user, false if not
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof User)) {
      return false;
    }

    User user = (User) o;

    return getUsername().equals(user.getUsername());
  }
}
