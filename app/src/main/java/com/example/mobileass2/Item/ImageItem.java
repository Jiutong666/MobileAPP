package com.example.mobileass2.Item;

public class ImageItem implements Item{
    private String imageUrl;
    private double latitude;
    private double longitude;
    private String title;
    private String userEmail;


    public ImageItem(String imageUrl, double latitude, double longitude, String title, String userEmail) {
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.userEmail = userEmail;
    }

    // Getters and potentially setters

    @Override
    public String toString() {
        return "ImageItem{" +
                "imageUrl='" + imageUrl + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", title='" + title + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }

    public String getImageUrl() {
        return imageUrl;
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
