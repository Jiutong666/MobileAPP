package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        renderMarkers("text.json");
        renderMarkers("image.json");
        renderMarkers("video.json");
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
    }

    private void renderMarkers(String fileName) {
        List<Location> locations = readLocationsFromInternalStorage(fileName);
        if (locations == null || locations.isEmpty()) {
            Log.w(TAG, "No locations found in " + fileName);
            return;
        }

        // Add markers to the map
        for (Location location : locations) {
            LatLng latLng = new LatLng(location.latitude, location.longitude);
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location.title);
            mMap.addMarker(markerOptions);
        }

        // Move camera to the first location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locations.get(0).latitude, locations.get(0).longitude), 10));
    }

    private List<Location> readLocationsFromInternalStorage(String fileName) {
        JSONObject jsonObject = readDataFromInternalStorage(fileName);
        List<Location> locations = new ArrayList<>();
        if (jsonObject != null) {
            try {
                JSONArray locationsArray = jsonObject.getJSONArray("locations");
                for (int i = 0; i < locationsArray.length(); i++) {
                    JSONObject locationObject = locationsArray.getJSONObject(i);
                    Location location = new Location();
                    location.latitude = locationObject.getDouble("latitude");
                    location.longitude = locationObject.getDouble("longitude");
                    location.title = locationObject.getString("title");
                    location.type = locationObject.getString("type");
                    locations.add(location);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON data from " + fileName, e);
            }
        }
        return locations;
    }

    private JSONObject readDataFromInternalStorage(String fileName) {
        String dirName = "myData"; // 文件夹名
        File directory = new File(getActivity().getExternalFilesDir(null), dirName); // 获取应用的外部文件目录，并指定子目录名
        File file = new File(directory, fileName);

        Log.d(TAG, "readDataFromInternalStorage: " + file);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, n, StandardCharsets.UTF_8));
            }
            fis.close();

            String fileContent = stringBuilder.toString();
            Log.d(TAG, "Read File Content: " + fileContent); // 打印读取的文件内容

            return new JSONObject(fileContent);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error reading data from file", e);
        }
        return null;
    }

    // Sample data structure for location
    class Location {
        double latitude;
        double longitude;
        String title;
        String type; // "text", "image", or "video"
    }
}
