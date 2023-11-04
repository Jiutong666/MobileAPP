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

import com.example.mobileass2.Item.ImageItem;
import com.example.mobileass2.Item.Item;
import com.example.mobileass2.Item.TextIMaptem;
import com.example.mobileass2.Item.VideoItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private TextView markerTitleTextView;

    private HashMap<String, TextIMaptem> textsMap = new HashMap<>(); // 用来存储Text对象的HashMap
    private HashMap<String, ImageItem> imagesMap = new HashMap<>();
    private HashMap<String, VideoItem> videosMap = new HashMap<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void writeInItem(QueryDocumentSnapshot document, String itemType) {
        switch (itemType){
            case "text":
                TextIMaptem textIMaptem = new TextIMaptem(
                        document.getString("content"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                        document.getString("title"),
                        document.getString("userEmail")
                );
                // 将Text对象添加到HashMap中，以document的ID为键
                textsMap.put(document.getId(), textIMaptem);

            case "image":

                ImageItem imageItem = new ImageItem(
                        document.getString("imageUrl"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                        document.getString("title"),
                        document.getString("userEmail")
                );
                imagesMap.put(document.getId(), imageItem);

            case "video":
                VideoItem videoItem = new VideoItem(
                        document.getString("videoUrl"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                        document.getString("title"),
                        document.getString("userEmail")
                );
                videosMap.put(document.getId(), videoItem);
        }
    }



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
                            for (HashMap.Entry<String, TextIMaptem> entry : textsMap.entrySet()) {
                                Log.d(TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue().toString());
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
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
                            for (HashMap.Entry<String, ImageItem> entry : imagesMap.entrySet()) {
                                Log.d(TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue().toString());
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
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
                            for (HashMap.Entry<String, VideoItem> entry : videosMap.entrySet()) {
                                Log.d(TAG, "Key: " + entry.getKey() + " Value: " + entry.getValue().toString());
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }



    private void addMarkersToMap(HashMap<String, ? extends Item> itemsMap) {
        for (Map.Entry<String, ? extends Item> entry : itemsMap.entrySet()) {
            String id = entry.getKey(); // The ID from your map entry
            Item item = entry.getValue();
            LatLng position = new LatLng(item.getLatitude(), item.getLongitude());

            // Add a marker to the map with the position and title from the Item
            Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(item.getTitle())
                    // Here you can add more customization to your marker
            );

            // Set the ID as the tag for the marker
            marker.setTag(id);
        }
    }

    private void updateAllMarkers() {
        // Clear existing markers if necessary
        mMap.clear();

        // Add markers for all item types
//        addMarkersToMap(textsMap); // Add text item markers
//        addMarkersToMap(imagesMap); // Add image item markers
        addMarkersToMap(videosMap); // Add video item markers
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is ready");
        mMap = googleMap;
        LatLng sydney = new LatLng(37.4219983, -122.084);
        float zoomLevel = 15.0f; // 设置缩放级别为15
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));

//        mMap.setOnMarkerClickListener(marker -> {
//            markerTitleTextView.setText(marker.getTitle());
//            marker.showInfoWindow(); // 显示信息窗口
//            markerTitleTextView.setVisibility(View.VISIBLE); // 显示悬浮窗
//            return false;
//        });

        // 获取UI设置并启用缩放按钮
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        readItem();
        updateAllMarkers();

        //        mMap.addMarker(new MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//        );

        // 加载JSON文件并添加标记
//        addMarkersFromJson("text.json", BitmapDescriptorFactory.HUE_RED);
//        addMarkersFromJson("image.json", BitmapDescriptorFactory.HUE_BLUE);
//        addMarkersFromJson("video.json", BitmapDescriptorFactory.HUE_GREEN);


    }

    private String readFromFile(File file) {
        StringBuilder content = new StringBuilder();
        try {
            if (!file.exists()) {
                Log.e("FileRead", "文件不存在: " + file.getAbsolutePath());
                return "";
            }

            Log.d("FileRead", "正在读取文件: " + file.getAbsolutePath());

            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                Log.d("FileRead", "读取到内容: " + line);
            }
            br.close();
            isr.close();
            fis.close();
            Log.d("FileRead", "文件读取完成: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e("FileRead", "读取文件时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        Log.d("FileRead", "文件读取完成: " + content.toString());
        return content.toString();
    }


    private void addMarkersFromJson(String fileName, float color) {
        try {
            // 获取JSON文件的路径
            String dirName = "myData";
            File directory = new File(requireActivity().getExternalFilesDir(null), dirName);
            File file = new File(directory, fileName);
            // 读取JSON文件
            String json = readFromFile(file);
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject item = jsonObject.getJSONObject(key);
                double latitude = item.getDouble("latitude");
                double longitude = item.getDouble("longitude");
                String title = item.getString("title");
                Log.d(TAG, "Adding marker: " + title + ", " + latitude + ", " + longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(title)
                        .icon(BitmapDescriptorFactory.defaultMarker(color)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

}
