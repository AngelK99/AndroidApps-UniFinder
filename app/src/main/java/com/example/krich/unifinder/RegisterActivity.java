package com.example.krich.unifinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private EditText mPassCon;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Registration");
        setContentView(R.layout.activity_register);

        mEmail = (EditText)findViewById(R.id.email);
        mPass = (EditText)findViewById(R.id.password);
        mPassCon = (EditText)findViewById(R.id.passwordConfirm);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    protected void reg(View v){
        String email = mEmail.getText().toString();
        final String password = mPass.getText().toString();
        String passCon = mPassCon.getText().toString();
        PassHasher ph = new PassHasher();
        String hashedPass;

        if(!password.equals(passCon)){
            Toast.makeText(this, "Passwords should match!", Toast.LENGTH_SHORT).show();
            return;
        }

        hashedPass = ph.Hash(password);


        mAuth.createUserWithEmailAndPassword(email, hashedPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            DatabaseReference ref = mDatabase.getReference()
                                                            .child("users")
                                                            .child(user.getUid());
                            String eMail = user.getEmail();

                            ref.child("hasLogged").setValue("0");
                            ref.child("eMail").setValue(eMail);

                            Intent loginPage = new Intent(RegisterActivity.this, LoginActivity.class);
                            Bundle b = new Bundle();

                            String[] loginInfo = {eMail, password};
                            b.putStringArray("loginParams", loginInfo);

                            loginPage.putExtras(b);
                            startActivity(loginPage);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Reg failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
