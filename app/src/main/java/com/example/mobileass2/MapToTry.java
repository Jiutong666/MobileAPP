package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MapToTry extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_try);

        DocumentReference docRef = FirebaseFirestore.getInstance().document("images/3rZoQQ4IaSOZZpeVWIN0");

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // 打印特定字段
                    String name = documentSnapshot.getString("imageUrl");
                    Log.d("Firestore", "Name: " + name);

                    // 或者，打印整个文档数据
                    Map<String, Object> data = documentSnapshot.getData();
                    Log.d("Firestore", "Document data: " + data.toString());
                } else {
                    Log.d("Firestore", "Document does not exist!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error reading document", e);
            }
        });


    }
}
