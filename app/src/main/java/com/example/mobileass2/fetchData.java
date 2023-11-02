package com.example.mobileass2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;


public class fetchData extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "fetchData";
    private JSONObject textJson = new JSONObject();
    private JSONObject imageJson = new JSONObject();
    private JSONObject videoJson = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_to_try);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchDataAndSave();
        textJson = readDataFromInternalStorage("text.json");
        imageJson = readDataFromInternalStorage("image.json");
        videoJson = readDataFromInternalStorage("video.json");

    }

    private void fetchDataAndSave() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("texts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            JSONObject mainObject = new JSONObject(); // 主要用来存储所有数据的JSON对象
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                JSONObject docObject = new JSONObject(); // 用来存储单个document的数据
                                try {
                                    docObject.put("content", document.getString("content"));
                                    docObject.put("latitude", document.getDouble("latitude"));
                                    docObject.put("longitude", document.getDouble("longitude"));
                                    docObject.put("title", document.getString("title"));
                                    docObject.put("userEmail", document.getString("userEmail"));
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON error", e);
                                }
                                // 将单个document的数据添加到主JSON对象
                                try {
                                    mainObject.put(document.getId(), docObject);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            saveDataToInternalStorage(mainObject.toString(), "text");

                            // 打印保存的数据到日志
                            Log.d(TAG, "Text Data: " + mainObject.toString());

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        db.collection("images")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            JSONObject mainObject = new JSONObject(); // 主要用来存储所有数据的JSON对象
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                JSONObject docObject = new JSONObject(); // 用来存储单个document的数据
                                try {
                                    docObject.put("imageUrl", document.getString("imageUrl"));
                                    docObject.put("latitude", document.getDouble("latitude"));
                                    docObject.put("longitude", document.getDouble("longitude"));
                                    docObject.put("title", document.getString("title"));
                                    docObject.put("userEmail", document.getString("userEmail"));
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON error", e);
                                }
                                // 将单个document的数据添加到主JSON对象
                                try {
                                    mainObject.put(document.getId(), docObject);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            saveDataToInternalStorage(mainObject.toString(), "image");

                            // 打印保存的数据到日志
                            Log.d(TAG, "Image Data: " + mainObject.toString());

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        db.collection("videos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            JSONObject mainObject = new JSONObject(); // 主要用来存储所有数据的JSON对象
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                JSONObject docObject = new JSONObject(); // 用来存储单个document的数据
                                try {
                                    docObject.put("videoUrl", document.getString("videoUrl"));
                                    docObject.put("latitude", document.getDouble("latitude"));
                                    docObject.put("longitude", document.getDouble("longitude"));
                                    docObject.put("title", document.getString("title"));
                                    docObject.put("userEmail", document.getString("userEmail"));
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON error", e);
                                }
                                // 将单个document的数据添加到主JSON对象
                                try {
                                    mainObject.put(document.getId(), docObject);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            saveDataToInternalStorage(mainObject.toString(), "video");

                            // 打印保存的数据到日志
                            Log.d(TAG, "Video Data: " + mainObject.toString());

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }


    private void saveDataToInternalStorage(String jsonData, String fileN) {
        String dirName = "myData"; // 文件夹名
        String fileName = fileN + ".json"; // 文件名

        File directory = new File(getExternalFilesDir(null), dirName); // 获取应用的外部文件目录，并指定子目录名

        // 如果文件夹不存在，则创建它
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try {
            // 使用FileOutputStream以覆盖模式写入文件。如果文件不存在，它会被创建。
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonData.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "Error saving data to file", e);
        }
    }


    private JSONObject readDataFromInternalStorage(String fileN) {
        String dirName = "myData"; // 文件夹名
        String fileName = fileN; // 文件名
        File directory = new File(getExternalFilesDir(null), dirName); // 获取应用的外部文件目录，并指定子目录名
        File file = new File(directory, fileName);

        Log.d(TAG, "readDataFromInternalStorage: " + file);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, n, StandardCharsets.UTF_8));
            }
            fis.close();

            String fileContent = stringBuilder.toString();
            Log.d(TAG, "Read File Content: " + fileContent); // 打印读取的文件内容

            return new JSONObject(fileContent);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error reading data from file", e);
        }
        return null;
    }

    @Override
    public void onClick(View view) {

    }
}
