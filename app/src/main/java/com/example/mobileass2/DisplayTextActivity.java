package com.example.mobileass2;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayTextActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private TextView textContent, userName, noCommentsTextView;
    private ImageView userAvatar;
    private Button buttonSubmit, buttonBack, buttonHome;
    private ImageButton buttonLike;
    private EditText commentEditText;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentsList = new ArrayList<>();
    private FirebaseFirestore fireStore;
    private FirebaseAuth auth;

    private String packageId; // each package content has a unique ID
    private boolean isLikedByCurrentUser; //track if a user liked a post

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaytext);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize buttons and text fields
        textContent = findViewById(R.id.textViewComment);
        userName = findViewById(R.id.textViewUsername);
        userAvatar = findViewById(R.id.imageViewAvatar);
        buttonLike = findViewById(R.id.buttonLike);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        commentEditText = findViewById(R.id.editTextComment);
        buttonBack = findViewById(R.id.buttonBack);
        buttonHome = findViewById(R.id.buttonHome);
        noCommentsTextView = findViewById(R.id.noCommentsTextView);

        // Initialize the RecyclerView
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        // Set its LayoutManager
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLikeStatus();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Just finish the current activity and go back
            }
        });

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** need to finish!!!!!!
                 *  Navigate to the home activity (or main dashboard)
                // Intent intent = new Intent(PackageDisplayActivity.this, HomeActivity.class);
                // startActivity(intent);
                // finish();*/
            }
        });

        fetchDataFromFirebase();

        // Initialize the adapter and set it to the RecyclerView
        commentsAdapter = new CommentsAdapter(this, commentsList);
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    private void fetchDataFromFirebase() {
        // Assuming the document ID or package ID is passed as an extra
        packageId = getIntent().getStringExtra("PACKAGE_ID");

        // Fetch package details...
        fireStore.collection("texts").document(packageId)
                .get()
                .addOnSuccessListener(document -> {
        if (document.exists()) {
            String title = document.getString("title");
            String content = document.getString("content");
            double latitude = document.getDouble("latitude");
            double longitude = document.getDouble("longitude");
            String username = document.getString("userEmail");
            textContent.setText(title + "\n" + content);

            if (googleMap != null) {
                LatLng location = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(location)
                        .title("Package Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }

            userName.setText(username);
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
                        buttonLike.setImageResource(isLikedByCurrentUser ? R.drawable.liked : R.drawable.like);
                    }
                });
        // Fetch comments of the package
        fetchCommentsFromFirebase();
    }

    private void fetchCommentsFromFirebase() {
        fireStore.collection("comments")
                .whereEqualTo("packageId", packageId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        commentsList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Comment comment = document.toObject(Comment.class);
                            commentsList.add(comment);
                        }
                        commentsAdapter.notifyDataSetChanged();

                        // Check for empty comments
                        if (commentsList.isEmpty()) {
                            noCommentsTextView.setVisibility(View.VISIBLE);
                            commentsRecyclerView.setVisibility(View.GONE);
                        } else {
                            noCommentsTextView.setVisibility(View.GONE);
                            commentsRecyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "Error fetching comments", Toast.LENGTH_SHORT).show();
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
        buttonLike.setImageResource(isLikedByCurrentUser ? R.drawable.liked : R.drawable.like);
    }

    private void submitComment() {
        String commentText = commentEditText.getText().toString().trim();
        if (!commentText.isEmpty()) {
            Map<String, Object> commentData = new HashMap<>();
            commentData.put("userId", auth.getCurrentUser().getUid());
            commentData.put("packageId", packageId);
            commentData.put("textContent", commentText);
            commentData.put("timestamp", new Timestamp(new Date())); // Adding timestamp to order comments

            fireStore.collection("comments").add(commentData)
                    .addOnSuccessListener(documentReference -> {
                        commentEditText.setText(""); // Clear the input field
                        fetchCommentsFromFirebase();
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
