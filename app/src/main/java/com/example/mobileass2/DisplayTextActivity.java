package com.example.mobileass2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DisplayTextActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private TextView textContent, userName;
    private ImageView userAvatar;
    private Button likeButton, submitCommentButton;
    private EditText commentEditText;
    private RecyclerView commentsRecyclerView;
    private FirebaseFirestore fireStore;
    private FirebaseAuth auth;

    private String packageId; // assuming each package content has a unique ID
    private boolean isLikedByCurrentUser; // assuming you have a mechanism to track if a user liked a post

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaytext);

        // ... [rest of your initialization code]

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLikeStatus();
            }
        });

        submitCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();
            }
        });

        // Fetch the data here...
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        // Assuming the document ID or package ID is passed as an extra
        packageId = getIntent().getStringExtra("PACKAGE_ID");

        // Fetch package details...
        fireStore.collection("packages").document(packageId)
                .get()
                .addOnSuccessListener(document -> {
        if (document.exists()) {
            String content = document.getString("content");
            double latitude = document.getDouble("latitude");
            double longitude = document.getDouble("longitude");
            textContent.setText(content);

            if (googleMap != null) {
                LatLng location = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(location).title("Package Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }

            // Fetch user details, likes, and comments...
        }
    });

        // Determine if the current user has liked this post...
        fireStore.collection("likes")
                .whereEqualTo("userId", auth.getCurrentUser().getUid())
                .whereEqualTo("packageId", packageId)
                .get()
                .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            isLikedByCurrentUser = !task.getResult().isEmpty();
            likeButton.setText(isLikedByCurrentUser ? "Unlike" : "Like");
        }
    });
    }

    private void toggleLikeStatus() {
        if (isLikedByCurrentUser) {
            // Remove the like from Firestore
            fireStore.collection("likes")
                    .whereEqualTo("userId", auth.getCurrentUser().getUid())
                    .whereEqualTo("packageId", packageId)
                    .get()
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        fireStore.collection("likes").document(document.getId()).delete();
                    }
                }
            });
        } else {
            // Add the like to Firestore
            Map<String, Object> likeData = new HashMap<>();
            likeData.put("userId", auth.getCurrentUser().getUid());
            likeData.put("packageId", packageId);
            fireStore.collection("likes").add(likeData);
        }
        isLikedByCurrentUser = !isLikedByCurrentUser;
        likeButton.setText(isLikedByCurrentUser ? "Unlike" : "Like");
    }

    private void submitComment() {
        String commentText = commentEditText.getText().toString().trim();
        if (!commentText.isEmpty()) {
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("userId", auth.getCurrentUser().getUid());
            commentData.put("packageId", packageId);
            commentData.put("text", commentText);
            commentData.put("timestamp", new Timestamp(new Date())); // Adding timestamp to order comments

            fireStore.collection("comments").add(commentData)
                    .addOnSuccessListener(documentReference -> {
                commentEditText.setText(""); // Clear the input field
                // Optionally refresh comments or add the comment to the list in the UI
            })
            .addOnFailureListener(e -> {
                // Handle the error
                Toast.makeText(this, "Error posting comment", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        // Fetch the location data and display it on the map here...
    }
}
