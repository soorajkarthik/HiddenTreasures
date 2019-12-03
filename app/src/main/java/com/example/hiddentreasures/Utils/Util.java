package com.example.hiddentreasures.Utils;

public class Util {

    public static String getTimeDifferenceText(long oldTime) {

        long currentTime = System.currentTimeMillis();
        long difference = currentTime - oldTime;

        long years = difference / ((long) 365 * 24 * 60 * 60 * 1000);
        long months = difference / ((long) 30 * 24 * 60 * 60 * 1000);
        long weeks = difference / (7 * 24 * 60 * 60 * 1000);
        long days = difference / (24 * 60 * 60 * 1000);
        long hours = difference / (60 * 60 * 1000);
        long minutes = difference / (60 * 1000);

        if (years > 0) {

            return years + " years(s) ago";
        } else if (months > 0) {

            return months + " month(s) ago";
        } else if (weeks > 0) {

            return weeks + " week(s) ago";
        } else if (days > 0) {

            return days + " day(s) ago";
        } else if (hours > 0) {

            return hours + " hour(s) ago";
        } else if (minutes > 0) {

            return minutes + " minute(s) ago";
        } else {

            return "Just now";
        }
    }
}
