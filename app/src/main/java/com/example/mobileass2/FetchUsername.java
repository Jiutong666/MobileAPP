package com.example.mobileass2;

public interface FetchUsername {
    void onGet(String userEmail);
    void onError(Exception e);
}
