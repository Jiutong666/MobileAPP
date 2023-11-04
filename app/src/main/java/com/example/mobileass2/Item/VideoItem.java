package com.example.mobileass2.Item;

public class VideoItem implements Item {
    private String videoUrl;
    private double latitude;
    private double longitude;
    private String title;
    private String userEmail;

    public VideoItem(String videoUrl, double latitude, double longitude, String title, String userEmail) {
        this.videoUrl = videoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.userEmail = userEmail;
    }

    // Getters and potentially setters

    @Override
    public String toString() {
        return "VideoItem{" +
                "videoUrl='" + videoUrl + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", title='" + title + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }

    public String getVideoUrl() {
        return videoUrl;
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
