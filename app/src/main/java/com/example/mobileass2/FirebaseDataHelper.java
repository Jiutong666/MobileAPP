package com.example.mobileass2;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mobileass2.Item.MapItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataHelper {

    private FirebaseDatabase database;
    private DatabaseReference mapItemRef;

    public FirebaseDataHelper() {
        database = FirebaseDatabase.getInstance();
        mapItemRef = database.getReference("mapItem");
    }

    public void fetchMapItems(DataSnapshotCallback callback) {
        mapItemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FirebaseDataHelper", "Raw DataSnapshot: " + snapshot.toString());
                List<MapItem> mapItems = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    MapItem mapItem = postSnapshot.getValue(MapItem.class);
                    mapItems.add(mapItem);
                }
                callback.onDataReceived(mapItems);
                Log.d("FirebaseDataHelper", "Fetched map items: " + mapItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FirebaseDataHelper", "Error fetching map items: " + error.getMessage());
            }
        });
    }

    public interface DataSnapshotCallback {
        void onDataReceived(List<MapItem> mapItems);
    }
}
