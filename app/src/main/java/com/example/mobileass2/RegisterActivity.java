package com.example.mobileass2;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    public Button btnRegister;
    public TextView textViewLogin;
    public EditText email;
    public EditText username;
    public EditText password;
    public FirebaseAuth firebaseAuth;

    public FirebaseFirestore fireStore;
    public String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();



        email=findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        btnRegister = findViewById(R.id.registerUser);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String getEmail = email.getText().toString().trim();
               String getUsername = username.getText().toString().trim();
               String getPassword = password.getText().toString().trim();


               if(TextUtils.isEmpty(getEmail)){
                   email.setError("Please enter email");
                   return;
               }

                // Verify the format of the email address
                if (!Patterns.EMAIL_ADDRESS.matcher(getEmail).matches()) {
                    email.setError("Please enter a valid email address");
                    return;
                }
                firebaseAuth.fetchSignInMethodsForEmail(getEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            if (result.getSignInMethods().size() > 0) {
                                // The email is already registered
                                email.setError("This email is already registered");
                                return;
                            } else {
                                // The email is not registered, you can proceed with the registration
                                if(TextUtils.isEmpty(getUsername)){
                                    username.setError("Please enter username");
                                    return;
                                }
                                if(TextUtils.isEmpty(getPassword)){
                                    password.setError("Please enter password");
                                    return;
                                }
                                if(password.length()<6){
                                    password.setError("Password must be more than 6 Characters");
                                    return;
                                }
                                registerUser(getEmail,getPassword,getUsername);
                            }
                        } else {
                            Log.e("TAG", "Error checking if email is registered", task.getException());
                        }
                    }
                });




            }
        });

    }

    public void registerUser(String email, String password,String username){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userID= firebaseAuth.getUid();
                    DocumentReference documentReference = fireStore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("username",username);
                    user.put("email",email);
                    documentReference.set(user);

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);


                }else {
                    Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
