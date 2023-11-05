package com.example.mobileass2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class DropTextActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextInputEditText  contentEditText;

    private EditText titleEditText;
    private Button showLocationButton;

    private TextView textView;

    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_droptext);
        textView = findViewById(R.id.textView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize textInputEditText and showLocationButton
        titleEditText = findViewById(R.id.textInputEditText2);   // Assuming this is your title EditText
        contentEditText = findViewById(R.id.textInputEditText);
        showLocationButton = findViewById(R.id.buttonShowLocation);

        fireStore = FirebaseFirestore.getInstance();
        showLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from textInputEditText
                String titleText = titleEditText.getText().toString().trim();
                String contentText = contentEditText.getText().toString().trim();
                if (titleText.isEmpty() || contentText.isEmpty()) {
                    // Handle the case where either title or content or both are empty
                    Toast.makeText(DropTextActivity.this, "Please fill both fields.", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution if any input is empty
                }

                // Check if GoogleMap is initialized
                if (googleMap != null) {
                    try {
                        // Enable the "My Location" layer on the map
                        googleMap.setMyLocationEnabled(true);

                        // Start location updates (if needed)
                        startLocationUpdates();

                        // Display coordinates in the TextView
                        displayCoordinates();

                        // Retrieve current location, store data to Firestore and display marker
                        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();

                                    // Store the data in Firestore
                                    storeDataToFirestore(titleText, contentText, latitude, longitude);
                                    // Place a marker on the map
                                    LatLng currentLatLng = new LatLng(latitude, longitude);
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(currentLatLng)
                                            .title(titleText)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))); // Set marker color to purple

                                    // Move the camera to the current location
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                }
                            }
                        });

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });




        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, enable location features
            enableMyLocation();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Function to display coordinates in the TextView
    private void displayCoordinates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            // Format the coordinates as a string
                            String coordinates = "Latitude: " + latitude + "\nLongitude: " + longitude;
                            // Update the TextView with the coordinates
                            textView.setText(coordinates);
                        }
                    }
                });
    }

    private void storeDataToFirestore(String title, String content, double latitude, double longitude) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("content", content);
        data.put("latitude", latitude);
        data.put("longitude", longitude);
        data.put("userEmail", userEmail);
        data.put("likes", 0);

        fireStore.collection("texts").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(DropTextActivity.this, "Data stored successfully", Toast.LENGTH_SHORT).show();

                        // Get the ID of the created document and pass to the display page
                        String packageId = documentReference.getId(); // Send the ID to MainActivity and start it
                        Intent intent = new Intent(DropTextActivity.this, DisplayTextActivity.class);
                        intent.putExtra("PACKAGE_ID", packageId); // Pass the ID as an extra
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DropTextActivity.this, "Error storing data", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
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
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

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
}
