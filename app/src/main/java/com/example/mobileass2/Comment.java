package com.example.mobileass2;

import com.google.firebase.Timestamp;

public class Comment {
    private String username;
    private String commentText;
    private Timestamp commentTimestamp;

    public Comment(String username, String commentText, Timestamp commentTimestamp) {
        this.username = username;
        this.commentText = commentText;
        this.commentTimestamp = commentTimestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return commentText;
    }

    public Timestamp getTimestamp() {
        return commentTimestamp;
    }
}
