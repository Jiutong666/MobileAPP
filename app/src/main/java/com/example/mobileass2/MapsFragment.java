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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private TextView markerTitleTextView;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is ready");
        mMap = googleMap;
        LatLng sydney = new LatLng(37.4219983, -122.084);
        float zoomLevel = 15.0f; // 设置缩放级别为15
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));

        mMap.setOnMarkerClickListener(marker -> {
            markerTitleTextView.setText(marker.getTitle());
            marker.showInfoWindow(); // 显示信息窗口
            markerTitleTextView.setVisibility(View.VISIBLE); // 显示悬浮窗
            return false;
        });

        // 获取UI设置并启用缩放按钮
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        //        mMap.addMarker(new MarkerOptions()
//                .position(sydney)
//                .title("Marker in Sydney")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//        );

        // 加载JSON文件并添加标记
        addMarkersFromJson("text.json", BitmapDescriptorFactory.HUE_RED);
        addMarkersFromJson("image.json", BitmapDescriptorFactory.HUE_BLUE);
        addMarkersFromJson("video.json", BitmapDescriptorFactory.HUE_GREEN);


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
