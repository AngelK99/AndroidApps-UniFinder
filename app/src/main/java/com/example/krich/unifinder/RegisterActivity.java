package com.example.krich.unifinder;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private EditText mPassCon;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = (EditText)findViewById(R.id.email);
        mPass = (EditText)findViewById(R.id.password);
        mPassCon = (EditText)findViewById(R.id.passwordConfirm);
        mAuth = FirebaseAuth.getInstance();
    }

    protected void reg(View v){
        String email = mEmail.getText().toString();
        String password = mPass.getText().toString();
        String passCon = mPassCon.getText().toString();
        String hashedPass = "";

        if(!password.equals(passCon)){
            Toast.makeText(this, "Passwords should match!", Toast.LENGTH_SHORT).show();
            return;
        }

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
            Toast.makeText(RegisterActivity.this, "Exception",
                    Toast.LENGTH_SHORT).show();
        }

        mAuth.createUserWithEmailAndPassword(email, hashedPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Reg failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
