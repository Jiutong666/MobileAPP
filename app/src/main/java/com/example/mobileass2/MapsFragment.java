package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobileass2.Item.MapItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Collection;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;
    CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("texts");
//    private FirebaseDataHelper firebaseDataHelper;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        fetchDataAndAddMarkers();

    }

    private String TAG = "FirebaseFetch";

    private void fetchDataAndAddMarkers() {
        // Fetch data from "images" collection
//        db.collection("images")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String docId = document.getId();
//                            Double longitude = document.getDouble("longitude");
//                            Double latitude = document.getDouble("latitude");
//                            LatLng location = new LatLng(latitude, longitude);
//                            mMap.addMarker(new MarkerOptions().position(location).title(document.getId()));
//                            Log.d(TAG, "Images: Document ID: " + docId + ", Longitude: " + longitude + ", Latitude: " + latitude);
//
//                        }
//                    } else {
//                        Log.w(TAG, "Error getting documents.", task.getException());
//                    }
//                });

        // Fetch data from "texts" collection
        db.collection("texts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docId = document.getId();
                            Double longitude = document.getDouble("longitude");
                            Double latitude = document.getDouble("latitude");
                            LatLng location = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(location).title(document.getId()));
                            Log.d(TAG, "Texts: Document ID: " + docId + ", Longitude: " + longitude + ", Latitude: " + latitude);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fetchDataAndAddMarkers();
    }
}