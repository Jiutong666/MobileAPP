package com.example.mobileass2.Item;

public class TextIMaptem implements Item{
    private String content;
    private double latitude;
    private double longitude;
    private String title;
    private String userEmail;

    // Constructor
    public TextIMaptem(String content, double latitude, double longitude, String title, String userEmail) {
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "Text{" +
                "content='" + content + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", title='" + title + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
