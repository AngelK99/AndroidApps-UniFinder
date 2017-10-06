package com.example.krich.unifinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(LoginActivity.this);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    protected void login(View v){
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String hashedPass = "";

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(password.getBytes(StandardCharsets.US_ASCII));

            byte[] Hashed = sha1.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder buff = new StringBuilder();
            for (byte b : Hashed) {
                String conversion = Integer.toString(b & 0xFF,16);
                while (conversion.length() < 2) {
                    conversion = "0" + conversion;
                }
                buff.append(conversion);
            }
            hashedPass = buff.toString();
        }
        catch (NoSuchAlgorithmException e) {
            Toast.makeText(LoginActivity.this, "Exception",
                    Toast.LENGTH_SHORT).show();
        }

        mAuth.signInWithEmailAndPassword(email, hashedPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(mDatabase.child("users").child(user.getUid().toString()).child("hasLogged").equals("1")) {
                                Intent pp = new Intent(LoginActivity.this, ProfilePageActivity.class);
                                LoginActivity.this.startActivity(pp);
                            }
                            else{
                                Intent ps = new Intent(LoginActivity.this, FirstLoginActivity.class);
                                LoginActivity.this.startActivity(ps);
                            }
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
