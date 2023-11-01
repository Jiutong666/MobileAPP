package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MapToTry extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_try);


//        // read data from firebase
//        DocumentReference docRef = FirebaseFirestore.getInstance().document("images/3rZoQQ4IaSOZZpeVWIN0");
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    // 打印特定字段
//                    String name = documentSnapshot.getString("imageUrl");
//                    Log.d("Firestore", "Name: " + name);
//
//                    // 或者，打印整个文档数据
//                    Map<String, Object> data = documentSnapshot.getData();
//                    Log.d("Firestore", "Document data: " + data.toString());
//                } else {
//                    Log.d("Firestore", "Document does not exist!");
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.w("Firestore", "Error reading document", e);
//            }
//        });

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map<String, Object> user = new HashMap<>();
//        user.put("email", "www@123.com");
//        user.put("username", "www");
//
//        db.collection("users").add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });


        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("texts");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            // 获取latitude和longitude的值
                            Double latitude = document.getDouble("latitude");
                            Double longitude = document.getDouble("longitude");

                            // 这里处理或使用这些值
                            Log.d("Firestore", "Latitude: " + latitude + ", Longitude: " + longitude);
                        }
                    }
                } else {
                    Log.w("Firestore", "Error getting documents.", task.getException());
                }
            }
        });

    }
}