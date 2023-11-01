package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MapToTry extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String email = "linduan1018@gmail.com";
    String password = "123456";

    String TAG = "Permission Check";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_try);

//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // 登录成功
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            // 更新UI，如果需要的话
//                        } else {
//                            // 登录失败
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(MapToTry.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            // 更新UI，显示错误消息
//                        }
//                    }
//                });



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
