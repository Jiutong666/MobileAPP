package com.example.mobileass2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileass2.Adapter.UserAdapter;
import com.example.mobileass2.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> userList;

    public FirebaseFirestore fireStore;

    public FirebaseAuth firebaseAuth;

    public String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        readUsers();
        return view;
    }

    private void readUsers() {
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        // 获取整个 "users" 集合的引用
        CollectionReference usersCollectionRef = fireStore.collection("users");
        // 初始化适配器并设置到RecyclerView
        userAdapter = new UserAdapter(getContext(), userList, false);
        recyclerView.setAdapter(userAdapter);

        // 清空当前用户列表
        userList.clear();
        // 添加事件监听器来监听整个集合的变化
        usersCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("readUsers", "Listen failed.", e);
                    return;
                }

                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            // 将Firebase自动生成的ID设置为User对象的ID
                            user.setId(documentSnapshot.getId());
                            // 这里可以添加一个判断来排除当前用户
                            if (!user.getId().equals(firebaseAuth.getUid())) {
                                userList.add(user);
                            }
                        }
                    }
                    // 通知适配器有数据更改
                    userAdapter.notifyDataSetChanged();
                }
            }
        });
    }


}