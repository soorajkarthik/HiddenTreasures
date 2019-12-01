package com.example.hiddentreasures.Model;

//EXTEND COMPARABLE THEN USE COMPARETO METHOD TO ORDER USERS IN TERMS OF HOW MANY THINGS THEY HAVE FOUND
//ONLY ALLOW USERS TO ADD TO THINGS THEY HAVE FOUND IF THEY ARE WITHIN 50 FEET OF IT

import java.util.ArrayList;

public class User implements Comparable {

    private String email;
    private String username;
    private String password;

    private long lastSeen;

    private ArrayList<String> friendList;
    private ArrayList<FriendRequest> friendRequests;

    public User() {
        friendList = new ArrayList<>();
        friendRequests = new ArrayList<>();
    }

    public User(String email, String userName, String password) {
        this.email = email;
        this.username = userName;
        this.password = password;
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

    public void addFriendRequest(FriendRequest friendRequest) {
        friendRequests.add(friendRequest);
    }

    public void acceptFriendRequest(FriendRequest friendRequest) {
        friendRequests.remove(friendRequest);
        friendList.add(friendRequest.getUsername());
    }

    public ArrayList<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int calculateScore() {

        //add logic to calculate score
        return -1;
    }

    @Override
    public int compareTo(Object o) {
        return calculateScore() - ((User) o).calculateScore();
    }
}
