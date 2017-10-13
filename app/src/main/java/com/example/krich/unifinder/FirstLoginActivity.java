package com.example.krich.unifinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.krich.unifinder.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class FirstLoginActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUniRef;

    private EditText mFirstNameInput;
    private EditText mMidNameInput;
    private EditText mLastNameInput;
    private CheckBox mHiddenTelNum;
    private EditText mTelNumInput;
    private AutoCompleteTextView mUniInput;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);
        setTitle("Let's set up your account!");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mUserId = mAuth.getCurrentUser().getUid().toString();
        mUniRef = mDatabase.child("universities");

        mFirstNameInput = (EditText) findViewById(R.id.fNameInput);
        mMidNameInput = (EditText) findViewById(R.id.mNameInput);
        mLastNameInput = (EditText) findViewById(R.id.lNameInput);
        mTelNumInput = (EditText) findViewById(R.id.telInput);
        mHiddenTelNum = (CheckBox) findViewById(R.id.telHidden);
        mUniInput = (AutoCompleteTextView)findViewById(R.id.uniInput);

        mUniInput.addTextChangedListener(getUniIputTextWatcher());
    }

    protected void setProfile(View v){
        User userData;
        String fName = mFirstNameInput.getText().toString();
        String mName = mMidNameInput.getText().toString();
        String lName = mLastNameInput.getText().toString();
        String Sex;
        String uni = mUniInput.getText().toString();
        RadioButton sexM = (RadioButton)findViewById(R.id.sexM);

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

        if(uni.isEmpty()){
            Toast.makeText(this, "University is required!", Toast.LENGTH_LONG).show();
            mUniInput.requestFocus();
            return;
        }

        if(fName.length() > 20){
            Toast.makeText(this, "First Name must be between 1 and 20 symbols!", Toast.LENGTH_LONG).show();
            mFirstNameInput.requestFocus();
            return;
        }

        if(mName.length() > 20){
            Toast.makeText(this, "Middle Name must be between 1 and 20 symbols!", Toast.LENGTH_LONG).show();
            mMidNameInput.requestFocus();
            return;
        }
        if(lName.length() > 20){
            Toast.makeText(this, "Last Name must be between 1 and 20 symbols!", Toast.LENGTH_LONG).show();
            mLastNameInput.requestFocus();
            return;
        }
        if(uni.length() > 50){
            Toast.makeText(this, "Please enter a valid university!", Toast.LENGTH_LONG).show();
            mUniInput.requestFocus();
            return;
        }




        if(sexM.isChecked()){
            Sex = "m";
        } else{
            Sex = "f";
        }

        userData = new User(fName, mName, lName, phoneNum, isHidden, Sex, uni);

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
                    Toast.makeText(FirstLoginActivity.this,
                            "Upload Failed!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(FirstLoginActivity.this,
                            "Upload Success!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    private TextWatcher getUniIputTextWatcher(){
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUniRef.orderByValue()
                        .startAt(s.toString())
                        .endAt(s.toString() + "~")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> values = new ArrayList<String>();

                        for (DataSnapshot child:
                                dataSnapshot.getChildren()) {
                            values.add(child.getValue().toString());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FirstLoginActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                values);
                        mUniInput.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}