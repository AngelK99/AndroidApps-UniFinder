package com.example.krich.unifinder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.CheckedInputStream;

public class FirstLoginActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private StorageReference mStorageRef;



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
        mStorageRef = FirebaseStorage.getInstance().getReference();
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

        userData = new User(fName, mName, lName, phoneNum, isHidden);

        mDatabase.child("users").child(mUserId).setValue(userData);

        Intent pp = new Intent(FirstLoginActivity.this, HomePageActivity.class);
        FirstLoginActivity.this.startActivity(pp);

        finish();
    }

    public void profilePic(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            Uri img = data.getData();
            StorageReference profilePicRef = mStorageRef.child("profilePics").child(mUserId);

            Task uploadTask = profilePicRef.putFile(img);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(FirstLoginActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    String downloadUrl = taskSnapshot.getDownloadUrl().getPath();
                    Toast.makeText(FirstLoginActivity.this, "Upload Success!", Toast.LENGTH_SHORT).show();
                    Log.d("PicInfo ", downloadUrl);
                }
            });
        }
    }
}