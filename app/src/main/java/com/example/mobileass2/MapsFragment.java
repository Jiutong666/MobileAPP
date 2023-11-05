package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mobileass2.Item.MapItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;


public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private TextView markerTitleTextView;
    private TextView markerDscrpTextView;

    private HashMap<String, MapItem> textsMap = new HashMap<>(); // 用来存储Text对象的HashMap
    private HashMap<String, MapItem> imagesMap = new HashMap<>();
    private HashMap<String, MapItem> videosMap = new HashMap<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // 第一次从数据库读取全部数据
    public void writeInItem(QueryDocumentSnapshot document, String itemType) {
        switch (itemType){
            case "text":
                MapItem textMapItem = new MapItem(
                        "text",
                        document.getString("content"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                        document.getString("title"),
                        document.getString("userEmail")
                );
                // 将Text对象添加到HashMap中，以document的ID为键
                textsMap.put(document.getId(), textMapItem);
                break;

            case "image":
                MapItem imageItem = new MapItem(
                        "image",
                        document.getString("imageUrl"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                        document.getString("title"),
                        document.getString("userEmail")
                );
                imagesMap.put(document.getId(), imageItem);
                break;

            case "video":
                MapItem videoItem = new MapItem(
                        "video",
                        document.getString("videoUrl"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                        document.getString("title"),
                        document.getString("userEmail")
                );
                videosMap.put(document.getId(), videoItem);
                break;
        }
    }

    public String TAG1= "readItem";

    public void readItem() {
        db.collection("texts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                writeInItem(document, "text");
                            }
                            // 打印获取到的数据到日志，用于调试
                            for (HashMap.Entry<String, MapItem> entry : textsMap.entrySet()) {
                                Log.d(TAG1, "Key: " + entry.getKey() + " Value: " + entry.getValue().toString());
                            }

//                            addMarkersToMap(textsMap);
                        } else {
                            Log.w(TAG1, "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("images")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                        writeInItem(document, "image");
                            }
                            // 打印获取到的数据到日志，用于调试
                            for (HashMap.Entry<String, MapItem> entry : imagesMap.entrySet()) {
                                Log.d(TAG1, "Key: " + entry.getKey() + " Value: " + entry.getValue().toString());
                            }

//                            addMarkersToMap(imagesMap);

                        } else {
                            Log.w(TAG1, "Error getting documents.", task.getException());
                        }
                    }
                });

        db.collection("videos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                writeInItem(document, "video");
                            }

                            // 打印获取到的数据到日志，用于调试
                            for (HashMap.Entry<String, MapItem> entry : videosMap.entrySet()) {
                                Log.d(TAG1, "Key: " + entry.getKey() + " Value: " + entry.getValue().toString());
                            }

//                            addMarkersToMap(videosMap);
                        } else {
                            Log.w(TAG1, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // 实时监听数据库变化
    public void startListeningForTextItems() {
        db.collection("texts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        handleDocumentChanges(snapshots.getDocumentChanges(), "text");
                        Log.d(TAG, "Realtime update received.");
                    }
                });

        db.collection("images")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        handleDocumentChanges(snapshots.getDocumentChanges(), "image");
                        Log.d(TAG, "Realtime update received.");
                    }
                });

        db.collection("videos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        handleDocumentChanges(snapshots.getDocumentChanges(), "video");
                        Log.d(TAG, "Realtime update received.");
                    }
                });
    }

    // 这个方法处理收到的所有文档变化
    private void handleDocumentChanges(List<DocumentChange> documentChanges, String type) {
        for (DocumentChange dc : documentChanges) {
            QueryDocumentSnapshot document = dc.getDocument();
            switch (dc.getType()) {
                case ADDED:
                case MODIFIED:

                    writeInItem(document, type);
                    if (type.equals("video")){
                        Log.d("monitorChange", videosMap.get(document.getId()).toString());
                    }
                    break;
                case REMOVED:
                    removeItem(document.getId(), type);
                    break;
            }
        }

        mapRender();
    }

    // 在hashMap中删除数据库中没有的数据
    private void removeItem(String id, String type) {
        switch (type) {
            case "text":
                textsMap.remove(id);
                break;
            case "image":
                imagesMap.remove(id);
                break;
            case "video":
                videosMap.remove(id);
                break;
        }
    }

    public void mapRender() {
        mMap.clear();
        addMarkersToMap(textsMap); // Add text item markers
        addMarkersToMap(imagesMap); // Add image item markers
        addMarkersToMap(videosMap); // Add video item markers
    }


    private void addMarkersToMap(HashMap<String, MapItem> itemsMap) {
        for (HashMap.Entry<String, MapItem> entry : itemsMap.entrySet()) {
            String id = entry.getKey(); // The ID from your map entry
            MapItem item = entry.getValue();
            LatLng position = new LatLng(item.getLatitude(), item.getLongitude());

            // Add a marker to the map with the position and title from the Item
            Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(item.getType())
                    // Here you can add more customization to your marker
            );

            // Set the ID as the tag for the marker
            marker.setTag(id);

            Log.d("addMarkersToMap", "Marker added with ID: " + id + " at position: " + position);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "Map is ready");
        mMap = googleMap;
        LatLng sydney = new LatLng(37.4219983, -122.084);
        float zoomLevel = 10.0f; // 设置缩放级别为15
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));


        // 获取UI设置并启用缩放按钮
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        readItem();
        mapRender();

        Log.d("textIfThere", "program has been here");

        startListeningForTextItems();

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
        markerTitleTextView = view.findViewById(R.id.marker_title);
        markerDscrpTextView = view.findViewById(R.id.marker_description);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
}
