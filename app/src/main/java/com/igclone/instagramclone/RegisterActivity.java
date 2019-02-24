package com.igclone.instagramclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button register;
    TextView text_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUI();

        auth = FirebaseAuth.getInstance();

        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Registering...");
                progressDialog.show();


                String my_username = username.getText().toString();
                String my_fullname = fullname.getText().toString();
                String my_email = email.getText().toString();
                String my_password = password.getText().toString();

                if (TextUtils.isEmpty(my_username) || TextUtils.isEmpty(my_fullname)
                        || TextUtils.isEmpty(my_email) || TextUtils.isEmpty(my_password)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (my_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Passwird must have 6 characters", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    register(my_username, my_fullname, my_email, my_password);
                }
            }
        });


    }

    private void register(final String username, final String fullname, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userID);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio", "");
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/instagramclone-bc440.appspot.com/o/person-logo-png-3.png?alt=media&token=20b89099-fca1-4cc9-9616-e0efe970c6d2");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "The email or password is invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setupUI() {
        username = findViewById(R.id.register_username);
        fullname = findViewById(R.id.register_fullname);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        register = findViewById(R.id.register_btn);
        text_login = findViewById(R.id.text_login);
    }
}
