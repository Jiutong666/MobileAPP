package com.example.mobileass2;

import static android.content.ContentValues.TAG;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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
    private RecyclerView.Adapter commentsAdapter;
    private List<Comment> commentsList = new ArrayList<>();
    private FirebaseFirestore fireStore;
    private FirebaseAuth auth;
    private String packageId; // each package content has a unique ID
    private String user;
    private DocumentReference textRef;
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

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize the RecyclerView
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setHasFixedSize(true);
        // Set its LayoutManager
        LinearLayoutManager l = new LinearLayoutManager(this);
        l.setStackFromEnd(true);
        commentsRecyclerView.setLayoutManager(l);

        Comment c = new Comment("mock user", "no", Timestamp.now());
        commentsList.add(c);

        // Initialize the adapter and set it to the RecyclerView
        commentsAdapter = new CommentsAdapter(this, commentsList);
        commentsRecyclerView.setAdapter(commentsAdapter);

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        // Assuming the document ID or package ID is passed as an extra
        packageId = getIntent().getStringExtra("PACKAGE_ID");

        // Fetch package details...
        fireStore.collection("texts").document(packageId)
                .get()
                .addOnSuccessListener(document -> {
        if (document.exists()) {
            textRef = fireStore.collection("texts").document(packageId);

            String title = document.getString("title");
            String content = document.getString("content");
            double latitude = document.getDouble("latitude");
            double longitude = document.getDouble("longitude");
            String userId = auth.getCurrentUser().getUid();
            fetchUsername(userId);
            textContent.setText(title + "\n" + content);

            if (googleMap != null) {
                LatLng location = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(location)
                        .title("Package Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }

            userName.setText(user);
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
//        fetchCommentsFromFirebase();
    }

    private void fetchUsername(String userId) {
        fireStore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("username")) {
                        user = documentSnapshot.getString("username");
                    } else {
                        Toast.makeText(this, "Error fetching username", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "User not exist.", Toast.LENGTH_SHORT).show();
                });
    }



    private void fetchCommentsFromFirebase() {
        fireStore.collection("comments")
                .whereEqualTo("packageId", packageId)
//                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        commentsList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String userId = document.getString("userId");
                            String content = document.getString("textContent");
                            Timestamp timestamp = document.getTimestamp("timestamp");

                            // Get username from database; /************************/

                            Comment comment = new Comment(userId, content, timestamp);
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
                        Exception e = task.getException();
                        System.out.println("Fetch unsuccessful: " + e.getMessage());
                        e.printStackTrace(); // This will print the stack trace to the console
                        Toast.makeText(this, "Error fetching comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void toggleLikeStatus() {
        String userId = auth.getCurrentUser().getUid();

        if (isLikedByCurrentUser) {
            // Remove the like from Firestore
            fireStore.collection("likes")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("packageId", packageId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                fireStore.collection("likes").document(document.getId()).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "unlike successful!", Toast.LENGTH_SHORT).show();
                                            // decrement the number of likes
                                            textRef.update("likes", FieldValue.increment(-1));
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error unlike", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    });

        } else {
            // Add the like to Firestore
            Map<String, Object> likeData = new HashMap<>();
            likeData.put("userId", userId);
            likeData.put("packageId", packageId);
            fireStore.collection("likes").add(likeData)
                    .addOnSuccessListener(documentReference -> {
                        // Increment the number of likes
                        textRef.update("likes", FieldValue.increment(1));
                        Toast.makeText(this, "like successful!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error like", Toast.LENGTH_SHORT).show();
                    });
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
                        Toast.makeText(this, "Comment successful!", Toast.LENGTH_SHORT).show();
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
