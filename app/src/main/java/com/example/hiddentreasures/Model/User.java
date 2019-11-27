package com.example.hiddentreasures.Model;

//EXTEND COMPARABLE THEN USE COMPARETO METHOD TO ORDER USERS IN TERMS OF HOW MANY THINGS THEY HAVE FOUND
//ONLY ALLOW USERS TO ADD TO THINGS THEY HAVE FOUND IF THEY ARE WITHIN 50 FEET OF IT

public class User {

    public String email;
    public String username;
    public String password;

    public User() {

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
}
