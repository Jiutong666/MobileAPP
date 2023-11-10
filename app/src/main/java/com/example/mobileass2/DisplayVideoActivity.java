package com.example.mobileass2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.widget.VideoView;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DisplayVideoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private TextView userName, noCommentsTextView;
    private VideoView videoContent;
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
    private DocumentReference textRef;
    private boolean isLikedByCurrentUser; //track if a user liked a post


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displayvideo);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize buttons and text fields
        videoContent = findViewById(R.id.videoViewContent);
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
                Intent intent = new Intent(DisplayVideoActivity.this, MainActivity.class);

                // Clear all the activities on top of the main activity and create a new task
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // Close the current activity after starting the main activity
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchDataFromFirebase();
        fetchCommentsFromFirebase();
    }

    private void fetchDataFromFirebase() {
        // Assuming the document ID or package ID is passed as an extra
        packageId = getIntent().getStringExtra("PACKAGE_ID");
        Toast.makeText(this, packageId, Toast.LENGTH_SHORT).show();

        // Fetch package details...
        fireStore.collection("videos").document(packageId)
                .get()
                .addOnSuccessListener(document -> {
        if (document.exists()) {
            textRef = fireStore.collection("videos").document(packageId);

            String title = document.getString("title");
            String videoUrl = document.getString("videoUrl");
            double latitude = document.getDouble("latitude");
            double longitude = document.getDouble("longitude");
            String userId = document.getString("userId");
            fetchAvatarImage(userId);

            // Setup the video
            setupVideoPlayer(videoUrl);

            if (googleMap != null) {
                LatLng location = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(location)
                        .title("Package Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            }

            // Fetch the username from the firebase
            fetchUsername(userId, new UsernameCallback() {
                @Override
                public void onCallback(String username) {
                    // Use the username here
                    String user = username;
                    userName.setText(user + "\n" + title);
                }

                @Override
                public void onError(String error) {
                    // Handle the error here
                    System.out.println("Error: " + error);
                }
            });
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
    }

    private void setupVideoPlayer(String videoUrl) {
        Uri videoUri = Uri.parse(videoUrl);
        videoContent.setVideoURI(videoUri);
        videoContent.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoContent.start();
            }
        });
        videoContent.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
    }

    // Make sure to pause the video when the activity is not in the foreground
    @Override
    protected void onPause() {
        super.onPause();
        if (videoContent != null && videoContent.isPlaying()) {
            videoContent.pause();
        }
    }
    // Optionally, resume the video when the activity comes back to the foreground
    @Override
    protected void onResume() {
        super.onResume();
        if (videoContent != null) {
            videoContent.start();
        }
    }

    private void fetchUsername(String userId, UsernameCallback callback) {
        fireStore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("username")) {
                        String name = documentSnapshot.getString("username");
                        callback.onCallback(name); // Use the callback to return the name
                    } else {
                        callback.onError("Error fetching username");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("User not exist.");
                });
    }

    private void fetchAvatarImage(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();
        // Assuming your images are stored in a folder named 'avatars' in Firebase Storage
        StorageReference avatarRef = storageRef.child("users/" + userId + "/profile.jpg");

        // Download directly into ImageView
        avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use Glide or Picasso to load the image
                Glide.with(userAvatar.getContext())
                        .load(uri)
                        .into(userAvatar);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("Firebase", "Error fetching avatar", exception);
            }
        });
    }

    private void fetchCommentsFromFirebase() {
        AtomicInteger pendingUsernameFetches = new AtomicInteger(0);

        fireStore.collection("comments")
                .whereEqualTo("packageId", packageId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        commentsList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String userId = document.getString("userId");
                            String content = document.getString("textContent");
                            Timestamp timestamp = document.getTimestamp("timestamp");

                            // Increment the count before starting the fetch operation
                            pendingUsernameFetches.incrementAndGet();

                            // Get username from database;
                            fetchUsername(userId, new UsernameCallback() {
                                @Override
                                public void onCallback(String username) {
                                    Comment comment = new Comment(username, content, timestamp);
                                    commentsList.add(comment);

                                    // Decrement the count and update the adapter if all fetches are done
                                    if (pendingUsernameFetches.decrementAndGet() == 0) {
                                        runOnUiThread(() -> {
                                            commentsAdapter.notifyDataSetChanged();
                                            checkCommentsEmpty(); // a method to handle visibility
                                        });
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    // Even if there's an error, we need to decrement the count
                                    if (pendingUsernameFetches.decrementAndGet() == 0) {
                                        runOnUiThread(() -> {
                                            commentsAdapter.notifyDataSetChanged();
                                            checkCommentsEmpty(); // a method to handle visibility
                                        });
                                    }
                                    System.out.println("Error: " + error);
                                }
                            });
                        }

                        // Handle the case when there are no comments to begin with
                        if (task.getResult().isEmpty()) {
                            checkCommentsEmpty();
                        }
                    } else {
                        Exception e = task.getException();
                        System.out.println("Fetch unsuccessful: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(this, "Error fetching comments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkCommentsEmpty() {
        if (commentsList.isEmpty()) {
            noCommentsTextView.setVisibility(View.VISIBLE);
            commentsRecyclerView.setVisibility(View.GONE);
        } else {
            noCommentsTextView.setVisibility(View.GONE);
            commentsRecyclerView.setVisibility(View.VISIBLE);
        }
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
