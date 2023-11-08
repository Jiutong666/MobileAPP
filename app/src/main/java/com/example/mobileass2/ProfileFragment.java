package com.example.mobileass2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileass2.Adapter.ListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public TextView email, username;

    public FirebaseFirestore fireStore;

    public FirebaseAuth firebaseAuth;

    public String userID;

    public String userEmail;

    public String userName;

    public List<Data> mdata;

    public List<Data> idata;
    public List<Data> vdata;

    public RecyclerView recyclerTest;

    public RecyclerView recyclerImage;


    public RecyclerView recyclerVideo;
    public List<String> titlesList;
    public List<String> imageList;

    public List<String> videoList;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 RecyclerView
        recyclerTest = view.findViewById(R.id.textTitle);

        recyclerImage = view.findViewById(R.id.imageTitle);

        recyclerVideo= view.findViewById(R.id.videoTitle);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        email = view.findViewById(R.id.email_text);
        username = view.findViewById(R.id.username_text);
        userID= firebaseAuth.getUid();



        titlesList = new ArrayList<>();
        imageList =new ArrayList<>();
        videoList=new ArrayList<>();

        mdata = new ArrayList<>();
        idata =new ArrayList<>();
        vdata =new ArrayList<>();

        DocumentReference documentReference = fireStore.collection("users").document(userID);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    userEmail = documentSnapshot.getString("email");
                    userName = documentSnapshot.getString("username");

                    email.setText(userEmail);
                    username.setText(userName);

                    findTextsByEmail("jan@email.com");
                    findImagesByEmail("jan@email.com");
                    findVideosByEmail("jan@email.com");

                }


            }
        });

        testData();
        imageData();
        videoData();


    }
    private void findTextsByEmail(String email) {
        fireStore.collection("texts")
                .whereEqualTo("userEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        titlesList.clear(); // 清除旧的标题数据
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId(); // Here you get the document's ID
                            // Now we fetch the title for each ID
                            fireStore.collection("texts").document(id).get().addOnCompleteListener(titleTask -> {
                                if (titleTask.isSuccessful()) {
                                    DocumentSnapshot titleDoc = titleTask.getResult();
                                    if (titleDoc.exists() && titleDoc.contains("title")) {
                                        String title = titleDoc.getString("title");
                                        titlesList.add(title); // 将标题添加到列表中

                                        // 更新数据后，重新初始化RecyclerView的数据
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(this::testData); // 确保在主线程中调用
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void findImagesByEmail(String email) {
        fireStore.collection("images")
                .whereEqualTo("userEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageList.clear(); // 清除旧的标题数据
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId(); // Here you get the document's ID
                            // Now we fetch the title for each ID
                            fireStore.collection("images").document(id).get().addOnCompleteListener(titleTask -> {
                                if (titleTask.isSuccessful()) {
                                    DocumentSnapshot titleDoc = titleTask.getResult();
                                    if (titleDoc.exists() && titleDoc.contains("title")) {
                                        String title = titleDoc.getString("title");
                                        imageList.add(title); // 将标题添加到列表中

                                        // 更新数据后，重新初始化RecyclerView的数据
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(this::imageData); // 确保在主线程中调用
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void findVideosByEmail(String email) {
        fireStore.collection("videos")
                .whereEqualTo("userEmail", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        videoList.clear(); // 清除旧的标题数据
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId(); // Here you get the document's ID
                            // Now we fetch the title for each ID
                            fireStore.collection("videos").document(id).get().addOnCompleteListener(titleTask -> {
                                if (titleTask.isSuccessful()) {
                                    DocumentSnapshot titleDoc = titleTask.getResult();
                                    if (titleDoc.exists() && titleDoc.contains("title")) {
                                        String title = titleDoc.getString("title");
                                        videoList.add(title); // 将标题添加到列表中

                                        // 更新数据后，重新初始化RecyclerView的数据
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(this::videoData); // 确保在主线程中调用
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public void testData() {
        mdata.clear();
        for (String title : titlesList) {
            Data data = new Data();
            data.re_title = title;
            mdata.add(data);
        }


        if (recyclerTest.getAdapter() == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerTest.setLayoutManager(layoutManager);
            ListAdapter adapter = new ListAdapter(mdata);
            recyclerTest.setAdapter(adapter);
        } else {

            recyclerTest.getAdapter().notifyDataSetChanged();
        }
    }

    public void imageData() {
        idata.clear();
        for (String title : imageList) {
            Data data = new Data();
            data.re_title = title;
            idata.add(data);
        }

        if (recyclerImage.getAdapter() == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerImage.setLayoutManager(layoutManager);
            ListAdapter adapter = new ListAdapter(idata);
            recyclerImage.setAdapter(adapter);
        } else {
            recyclerImage.getAdapter().notifyDataSetChanged();
        }
    }

    public void videoData() {
        vdata.clear();
        for (String title : videoList) {
            Data data = new Data();
            data.re_title = title;
            vdata.add(data);
        }

        if (recyclerVideo.getAdapter() == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerVideo.setLayoutManager(layoutManager);
            ListAdapter adapter = new ListAdapter(vdata);
            recyclerVideo.setAdapter(adapter);
        } else {
            recyclerVideo.getAdapter().notifyDataSetChanged();
        }
    }

}
