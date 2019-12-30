package com.example.hiddentreasures.Model;

//EXTEND COMPARABLE THEN USE COMPARETO METHOD TO ORDER USERS IN TERMS OF HOW MANY THINGS THEY HAVE FOUND
//ONLY ALLOW USERS TO ADD TO THINGS THEY HAVE FOUND IF THEY ARE WITHIN 50 FEET OF IT

import java.util.ArrayList;
import java.util.HashMap;

public class User implements Comparable {

    private String email;
    private String username;
    private String password;
    private long dateJoined;
    private long lastSeen;

    private ArrayList<String> friendList;
    private ArrayList<FriendRequest> friendRequests;
    private ArrayList<Integer> treasuresFoundTodayIDs;
    private HashMap<String, Integer> foundTreasures;

    public User() {
        friendList = new ArrayList<>();
        friendRequests = new ArrayList<>();
        treasuresFoundTodayIDs = new ArrayList<>();
        foundTreasures = new HashMap<String, Integer>() {{
            put("COMMON", 0);
            put("UNCOMMON", 0);
            put("RARE", 0);
            put("ULTRA_RARE", 0);
            put("LEGENDARY", 0);
        }};


    }

    public User(String email, String userName, String password, long dateJoined) {
        this.email = email;
        this.username = userName;
        this.password = password;
        this.dateJoined = dateJoined;

        friendList = new ArrayList<>();
        friendRequests = new ArrayList<>();
        treasuresFoundTodayIDs = new ArrayList<>();
        foundTreasures = new HashMap<String, Integer>() {{
            put("COMMON", 0);
            put("UNCOMMON", 0);
            put("RARE", 0);
            put("ULTRA_RARE", 0);
            put("LEGENDARY", 0);
        }};
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getDateJoined() {
        return dateJoined;
    }

    public void addFriendRequest(FriendRequest friendRequest) {
        friendRequests.add(friendRequest);
    }

    public void removeFriendRequest(FriendRequest friendRequest) {
        friendRequests.remove(friendRequest);
    }

    public void removeFriendRequestFromUser(String username) {
        friendRequests.removeIf(friendRequest -> friendRequest.getUsername().equals(username));
    }

    public void acceptFriendRequest(FriendRequest friendRequest) {
        friendRequests.remove(friendRequest);
        addFriend(friendRequest.getUsername());
    }

    public boolean hasFriendRequestFromUser(String username) {
        return friendRequests.stream().anyMatch(friendRequest -> friendRequest.getUsername().equals(username));
    }

    public boolean isFriendOfUser(String username) {
        return friendList.contains(username);
    }

    public void addFriend(String username) {
        friendList.add(username);
    }

    public void removeFriend(String username) {
        friendList.remove(username);
    }

    public ArrayList<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public ArrayList<Integer> getTreasuresFoundTodayIDs() {
        return treasuresFoundTodayIDs;
    }

    public HashMap<String, Integer> getFoundTreasures() {
        return foundTreasures;
    }

    public void addFoundTreasure(Treasure treasure) {
        treasuresFoundTodayIDs.add(treasure.getId());
        foundTreasures.put(treasure.getRarity(), foundTreasures.get(treasure.getRarity()) + 1);
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int calculateScore() {

        int score = 0;

        score += foundTreasures.get("COMMON") * 100;
        score += foundTreasures.get("UNCOMMON") * 200;
        score += foundTreasures.get("RARE") * 300;
        score += foundTreasures.get("ULTRA_RARE") * 400;
        score += foundTreasures.get("LEGENDARY") * 500;

        return score;

    }

    @Override
    public int compareTo(Object o) {
        return calculateScore() - ((User) o).calculateScore();
    }
}
