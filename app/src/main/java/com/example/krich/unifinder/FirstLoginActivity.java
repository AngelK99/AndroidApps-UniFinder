package com.example.krich.unifinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirstLoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);
        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid().toString();
    }

    protected void setProfile(View v){
        EditText fName = (EditText)findViewById(R.id.fNameInput);
        EditText mName = (EditText) findViewById(R.id.mNameInput);
        EditText lName = (EditText) findViewById(R.id.lNameInput);

        mDatabase.child("users").child(mUserId).child("fName").setValue(fName.getText().toString());
        mDatabase.child("users").child(mUserId).child("mName").setValue(mName.getText().toString());
        mDatabase.child("users").child(mUserId).child("lName").setValue(lName.getText().toString());
        mDatabase.child("users").child(mUserId).child("hasLogged").setValue("1");

        Intent pp = new Intent(FirstLoginActivity.this, ProfilePageActivity.class);
        FirstLoginActivity.this.startActivity(pp);

        finish();
    }
}
