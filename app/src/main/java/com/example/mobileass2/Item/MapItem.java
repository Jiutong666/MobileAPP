package com.example.mobileass2.Item;

public class MapItem {
    private String type;
    private String content;
    private String imageUrl;
    private String videoUrl;
    private double latitude;
    private double longitude;
    private String title;
    private String userEmail;

    private int likeNo;
    private int viewNo;
    private int commentNo;

    // Constructor
    public MapItem(String type, String content, double latitude, double longitude, String title, String userEmail) {
        this.type = type;
        this.content = content;
//        switch (type) {
//            case "text":
//                this.content = content;
//                break;
//            case "image":
//                this.imageUrl = content;
//                break;
//            case "video":
//                this.videoUrl = content;
//        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "MapItem{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", title='" + title + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", likeNo=" + likeNo +
                ", viewNo=" + viewNo +
                ", commentNo=" + commentNo +
                '}';
    }

    public void addLikeNo() {
        likeNo += 1;
    }

    public void addViewNo() {
        viewNo += 1;
    }

    public void addCommentNo() {
        commentNo += 1;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
