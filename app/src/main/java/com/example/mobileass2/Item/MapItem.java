package com.example.mobileass2.Item;

import java.io.Serializable;

public class MapItem implements Serializable {
    public String id;
    public String title;
    public double latitude;
    public double longitude;
    public String type;
    public String description;
    public String imageUrl;
    public String audioUrl;
    public String videoUrl;
    public int visitCount;
    public int likeCount;

    public MapItem() {
    }

    public MapItem(String title, double latitude, double longitude, String description) {
//        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
//        this.type = type;
        this.description = description;
//        this.visitCount = visitCount;
//        this.likeCount = likeCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

}
