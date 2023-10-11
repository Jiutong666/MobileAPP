package com.example.mobileass2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class DropVideoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 3;

    private EditText titleEditText;
    private VideoView videoView;
    private Button uploadButton;
    private ImageButton captureButton;
    private FirebaseFirestore fireStore;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropvideo);

        fireStore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        titleEditText = findViewById(R.id.textInputEditText);
        videoView = findViewById(R.id.videoView);
        uploadButton = findViewById(R.id.uploadButton);
        captureButton = findViewById(R.id.captureButton);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        captureButton.setOnClickListener(v -> captureVideo());
        uploadButton.setOnClickListener(v -> {
            if (videoView.getDuration() > 0) {
                uploadVideoToFirebase();
            } else {
                Toast.makeText(DropVideoActivity.this, "No video to upload!", Toast.LENGTH_SHORT).show();
            }
        });

        checkLocationPermission();
    }



    private void captureVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Intent captureVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (captureVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(captureVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            this.videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(MediaPlayer::start);
        }
    }

    private void uploadVideoToFirebase() {
        if (videoUri == null) {
            Toast.makeText(DropVideoActivity.this, "No video to upload!", Toast.LENGTH_SHORT).show();
            return;
        }

        String path = "videos/" + UUID.randomUUID() + ".mp4";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);

        storageReference.putFile(videoUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
            String videoURL = downloadUri.toString();
            String title = titleEditText.getText().toString().trim();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Ask for location permissions if they're not granted or handle it accordingly.
                Toast.makeText(DropVideoActivity.this, "Location permissions not granted.", Toast.LENGTH_SHORT).show();
                return;
            }

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    // Prepare the data to be saved
                    Map<String, Object> docData = new HashMap<>();
                    docData.put("title", title);
                    docData.put("videoUrl", videoURL);
                    docData.put("latitude", latitude);
                    docData.put("longitude", longitude);
                    docData.put("userEmail", userEmail);

                    // Save to Firestore
                    fireStore.collection("videos").document().set(docData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(DropVideoActivity.this, "Video and data stored successfully", Toast.LENGTH_SHORT).show();
                                startLocationUpdates();
                            })
                            .addOnFailureListener(e -> Toast.makeText(DropVideoActivity.this, "Error storing data", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(DropVideoActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(exception -> {
                Toast.makeText(DropVideoActivity.this, "Location fetch failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            });
        })).addOnFailureListener(exception -> {
            Toast.makeText(DropVideoActivity.this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        });
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable location features
                enableMyLocation();
            } else {
                // Permission denied, handle it accordingly (e.g., show a message)
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureVideo(); // Retrying the capture video intent
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableMyLocation() {
        // Check if GoogleMap is initialized
        if (googleMap != null) {
            try {
                // Enable the "My Location" layer on the map
                googleMap.setMyLocationEnabled(true);

                // Start location updates (if needed)
                startLocationUpdates();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000); // Update interval in milliseconds

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // Handle location updates here if needed
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    // Use the obtained location
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // Check if marker already exists, if not, add one
                    if (googleMap != null) {
                        // For simplicity, considering only one marker on the map (from your show location button)
                        if (googleMap.getProjection().getVisibleRegion().latLngBounds.contains(currentLatLng)) {
                            // If location is within visible region, no need to move camera. Just return.
                            return;
                        } else {
                            // If the location is not within the visible region, we'll clear the map,
                            // re-add the marker and move the camera
                            googleMap.clear();

                            String locationText = titleEditText.getText().toString();
                            googleMap.addMarker(new MarkerOptions()
                                    .position(currentLatLng)
                                    .title(locationText.isEmpty() ? "My Location" : locationText)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                            // Move the camera to the current location
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        }
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            // Handle the case where location permission is not granted
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // You can customize the map and add markers or other functionality here.
    }
    // ... (the rest of the methods remain unchanged, such as enableMyLocation, startLocationUpdates, onMapReady, etc.)

}

