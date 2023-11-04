package com.example.mobileass2;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMapManager {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore;

    public FirebaseMapManager() {
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
    }

    public void addMapData(String title, double latitude, double longitude, String description) {
        String userID = firebaseAuth.getUid();
        DocumentReference documentReference = fireStore.collection("mapItem").document(userID);

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("title", title);
        mapData.put("latitude", latitude);
        mapData.put("longitude", longitude);
        mapData.put("description", description);

        documentReference.set(mapData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore", "Document successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error writing document", e);
            }
        });
    }
}

