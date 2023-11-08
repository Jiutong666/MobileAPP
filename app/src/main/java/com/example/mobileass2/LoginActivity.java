package com.example.mobileass2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public Button btnLogin;
    public TextView registerUser;

    public EditText email;
    public EditText password;
    public FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();

        email=findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);


        //login
        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getEmail = email.getText().toString().trim();
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
                if(TextUtils.isEmpty(getPassword)){
                    password.setError("Please enter password");
                    return;
                }
                if(password.length()<6){
                    password.setError("Password must be more than 6 Characters");
                    return;
                }

                loginUser(getEmail,getPassword);


            }
        });

        //register
        registerUser = findViewById(R.id.registerUser);
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this ,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }



    public void loginUser(String email, String password){

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this ,MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Email or Password was Wrong", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}


