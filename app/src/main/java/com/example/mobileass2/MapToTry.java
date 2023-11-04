package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import com.google.gson.Gson;



public class MapToTry extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String email = "linduan1018@gmail.com";
    String password = "123456";

    String TAG = "Permission Check";

    String TAG1 = "Json read";

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_try);

        requestStoragePermission();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 登录成功
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // 更新UI，如果需要的话
                        } else {
                            // 登录失败
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MapToTry.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // 更新UI，显示错误消息
                        }
                    }
                });




        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> dataList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = new HashMap<>();
                                data.put("id", document.getId());
                                data.put("imageUrl", document.getString("imageUrl"));
                                data.put("latitude", document.getDouble("latitude"));
                                data.put("longitude", document.getDouble("longitude"));
                                data.put("title", document.getString("title"));
                                data.put("userEmail", document.getString("userEmail"));

                                dataList.add(data);
                                Log.d(TAG1, "Document data: " + data.toString());
                            }
                            saveDataAsJson(dataList);
                        } else {
                            Log.w(TAG1, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    public void saveDataAsJson(List<Map<String, Object>> dataList) {
        Gson gson = new Gson();
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        for (Map<String, Object> data : dataList) {
            String id = (String) data.get("id");
            dataMap.put(id, data);
        }
        String jsonString = gson.toJson(dataMap);
        File file = new File(getFilesDir(), "data.json");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jsonString);
            writer.close();
            Toast.makeText(this, "Data saved as JSON", Toast.LENGTH_SHORT).show();
            Log.d(TAG1, "Data saved at: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
        }
    }





    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // 权限已经被授予，你可以在这里执行写入操作
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，你可以在这里执行写入操作
            } else {
                // 权限被拒绝，你应该向用户解释为什么需要这个权限
            }
        }
    }
}
