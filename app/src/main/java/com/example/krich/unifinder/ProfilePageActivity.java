package com.example.krich.unifinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfilePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mAuth = FirebaseAuth.getInstance();
        mEmailView = (TextView) findViewById(R.id.userEmail);

        mEmailView.setText(mAuth.getCurrentUser().getEmail());
        Log.d("asdf: ", mAuth.getCurrentUser().getEmail());
    }
}
