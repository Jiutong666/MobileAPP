package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;
    private FirebaseDataHelper firebaseDataHelper;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        firebaseDataHelper = new FirebaseDataHelper();
        firebaseDataHelper.fetchMapItems(new FirebaseDataHelper.DataSnapshotCallback() {
            @Override
            public void onDataReceived(List<MapItem> mapItems) {
                for (MapItem item: mapItems) {
                    LatLng location = new LatLng(item.latitude, item.longitude);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(item.title));
                    marker.setTag(item);
                }
            }
        });

        mMap.setOnMarkerClickListener(marker -> {
            MapItem clickedItem = (MapItem) marker.getTag();
            //更新浮窗上的TextView
            TextView infoTextView = getView().findViewById(R.id.markerInfo);
            infoTextView.setText(clickedItem.description);
            //显示浮窗
            RelativeLayout infoWindow = getView().findViewById(R.id.infoWindow);
            infoWindow.setVisibility(View.VISIBLE);
            return false;
                }
        );

        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));

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

        // 使用SharedPreferences检查是否首次启动
        SharedPreferences prefs = getActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart) {
            FirebaseMapManager mapManager = new FirebaseMapManager();
            mapManager.addMapData("Sydney", -34, 151, "Description about Sydney");
//            System.out.println("1111");

            // 更新首次启动状态
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }
    }
}