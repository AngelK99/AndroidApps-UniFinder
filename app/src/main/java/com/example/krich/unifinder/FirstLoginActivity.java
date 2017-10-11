package com.example.krich.unifinder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.CheckedInputStream;

public class FirstLoginActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUserId;

    private EditText mFirstNameInput;
    private EditText mMidNameInput;
    private EditText mLastNameInput;
    private DatePicker mCalendar;
    private CheckBox mHiddenTelNum;
    private EditText mTelNumInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);
        setTitle("Let's set up your account!");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid().toString();


        mFirstNameInput = (EditText)findViewById(R.id.fNameInput);
        mMidNameInput = (EditText) findViewById(R.id.mNameInput);
        mLastNameInput = (EditText) findViewById(R.id.lNameInput);
        mCalendar = (DatePicker)findViewById(R.id.calendar);
        mTelNumInput = (EditText)findViewById(R.id.telInput);
        mHiddenTelNum = (CheckBox)findViewById(R.id.telHidden);

        /*
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1);*/
    }

    protected void setProfile(View v){
        User userData;
        String fName = mFirstNameInput.getText().toString();
        String mName = mMidNameInput.getText().toString();
        String lName = mLastNameInput.getText().toString();

        int day = mCalendar.getDayOfMonth();
        int month = mCalendar.getMonth();
        int year = mCalendar.getYear();

        String phoneNum = mTelNumInput.getText().toString();
        Boolean isHidden = mHiddenTelNum.isChecked();

        if(fName.isEmpty()){
            Toast.makeText(this, "First Name is required!", Toast.LENGTH_LONG).show();
            mFirstNameInput.requestFocus();
            return;
        }

        if(lName.isEmpty()){
            Toast.makeText(this, "Last Name is required!", Toast.LENGTH_LONG).show();
            mLastNameInput.requestFocus();
            return;
        }

        userData = new User(fName, mName, lName, true, phoneNum, isHidden);

        mDatabase.child("users").child(mUserId).setValue(userData);

        Intent pp = new Intent(FirstLoginActivity.this, HomePageActivity.class);
        FirstLoginActivity.this.startActivity(pp);

        finish();
    }
}