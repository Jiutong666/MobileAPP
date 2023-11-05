package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobileass2.Item.MapItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.Manifest;



public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private TextView markerTitleTextView;
    private TextView markerDscrpTextView;
    private TextView markerDistanceView;
    private Button showDetail;

    private HashMap<String, MapItem> textsMap = new HashMap<>(); // 用来存储Text对象的HashMap
    private HashMap<String, MapItem> imagesMap = new HashMap<>();
    private HashMap<String, MapItem> videosMap = new HashMap<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RelativeLayout floatingWindow;

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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
                    textsMap.remove(document.getId());
                    break;
            }
        }

        mapRender();
    }

    // 在hashMap中删除数据库中没有的数据
    private void removeItem(String id, String type) {
        Marker marker;
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
            Marker marker = null;

            if ("text".equals(item.getType())) {
                // Add a marker to the map with the position and title from the Item
                marker = mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(item.getType())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.text))
                        // Here you can add more customization to your marker
                );
            } else if ("image".equals(item.getType())) {
                // Add a marker to the map with the position and title from the Item
                marker = mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(item.getType())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.image))
                        // Here you can add more customization to your marker
                );
            } else if ("video".equals(item.getType())) {
                // Add a marker to the map with the position and title from the Item
                marker = mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(item.getType())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.video))
                        // Here you can add more customization to your marker
                );
            } else {
                // If none of the types match, you may want to handle it or continue
                continue;
            }

            if (marker != null) {
                marker.setTag(id);
                Log.d("addMarkersToMap", "Marker added with ID: " + id + " at position: " + position);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "Map is ready");
        mMap = googleMap;
        LatLng sydney = new LatLng(37.4219983, -122.084);
        float zoomLevel = 10.0f; // 设置缩放级别为15
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));

        mMap.setOnMarkerClickListener(marker -> {
            // 每当marker被点击时，检查并更新浮动窗口的可见性
            if (floatingWindow.getVisibility() == View.GONE) {
                floatingWindow.setVisibility(View.VISIBLE); // 如果之前是隐藏的，现在显示它
            }

            String markerType = marker.getTitle();
            String title;
            String content;
            String id = (String) marker.getTag();

            switch (markerType) {
                case "text":
                    title = textsMap.get(id).getTitle();
                    content = textsMap.get(id).getContent();
                    break;
                case "image":
                    title = imagesMap.get(id).getTitle();
                    content = imagesMap.get(id).getContent();
                    break;
                case "video":
                    title = videosMap.get(id).getTitle();
                    content = videosMap.get(id).getContent();
                    break;
                default:
                    title = "null";
                    content = "null";
            }

            // 确保你在类中已经定义了currentLocation
            if (currentLocation != null) {
                Log.d("currentLocation", "onMarkerClick: Current location is not null" + currentLocation);
                double endLatitude = marker.getPosition().latitude;
                double endLongitude = marker.getPosition().longitude;
                float[] results = new float[1];
                Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), endLatitude, endLongitude, results);
                float distanceInMeters = results[0];
                float distanceInKilometers = distanceInMeters / 1000;
                String distanceText = String.format(Locale.getDefault(), "%.2f km", distanceInKilometers);
                markerDistanceView.setText(distanceText);
                marker.showInfoWindow(); // 显示信息窗口
                markerDistanceView.setVisibility(View.VISIBLE); // 显示悬浮窗
            } else {
                Log.d("currentLocation", "onMarkerClick: Current location is null");
                markerDistanceView.setText("--");
                marker.showInfoWindow(); // 显示信息窗口
                markerDistanceView.setVisibility(View.VISIBLE); // 显示悬浮窗
            }


            markerTitleTextView.setText(title);
            marker.showInfoWindow(); // 显示信息窗口
            markerTitleTextView.setVisibility(View.VISIBLE); // 显示悬浮窗

            markerDscrpTextView.setText(content);
            marker.showInfoWindow();
            markerDscrpTextView.setVisibility(View.VISIBLE);

            return false;
        });

        // 获取UI设置并启用缩放按钮
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        readItem();
        mapRender();

        Log.d("textIfThere", "program has been here");
    }

    private void getCurrentLocation() {
        if (getContext() == null) return; // 在 Fragment 中，getContext() 可能会返回 null

        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // 请求位置权限
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        startListeningForTextItems();

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> { // 这里不需要this作为上下文
                    if (location != null) {
                        // 使用当前位置
                        currentLocation = location;
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error trying to get last GPS location");
                    e.printStackTrace();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，再次调用 getCurrentLocation
                getCurrentLocation();
            } else {
                // 权限被拒绝，向用户解释为何需要权限
            }
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
        markerDscrpTextView = view.findViewById(R.id.marker_description);
        markerDistanceView = view.findViewById(R.id.distance_text);

        // 请确保getActivity()不会返回null
        Context context = getActivity();
        if (context != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            getCurrentLocation(); // 获取当前位置
        }


        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 初始化你之前定义的视图...
        floatingWindow = view.findViewById(R.id.floating_window);
        showDetail = view.findViewById(R.id.right_icon);
        // 设置关闭按钮
        ImageButton closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏floating_window
                floatingWindow.setVisibility(View.GONE);
            }
        });
        showDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String packageId = documentReference.getId(); // Send the ID to MainActivity and start it
//                Intent intent = new Intent(DropTextActivity.this, DisplayTextActivity.class);
//                intent.putExtra("PACKAGE_ID", packageId); // Pass the ID as an extra
//                startActivity(intent);
            }
        });
    }



}
