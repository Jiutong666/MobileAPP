package com.example.mobileass2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    public Button btnRegister;
    public TextView textViewLogin;
    public EditText email;
    public EditText username;
    public EditText password;
    public FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();

        email=findViewById(R.id.email);
        password = findViewById(R.id.password);

        btnRegister = findViewById(R.id.registerUser);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("s");
               String getEmail = email.getText().toString();
               String getPassword = password.getText().toString();

               registerUser(getEmail,getPassword);


            }
        });


        //跳转到登录页面
        textViewLogin= findViewById(R.id.loginUser);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this ,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void registerUser(String email, String password){
        System.out.println(1111111);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    System.out.println("Registration successful");
                }else {
                    System.out.println("Registration failed: " + task.getException().getMessage());
                }

            }
        });
    }

}
