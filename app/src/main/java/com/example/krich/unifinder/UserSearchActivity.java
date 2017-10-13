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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.krich.unifinder.models.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserSearchActivity extends AppCompatActivity {

    private AutoCompleteTextView mSearchBar;
    private Button mSearchBtn;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private LinearLayout mUserList;
    private RadioButton mFNameSrcRB;
    private RadioButton mLNameSrcRB;
    private RadioButton mUniSrcRb;
    private String mCurrUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        setTitle("Find People");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mCurrUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mSearchBar = (AutoCompleteTextView)findViewById(R.id.searchBar);
        mSearchBtn = (Button)findViewById(R.id.searchBtn);
        mUserList = (LinearLayout)findViewById(R.id.resultPanel);
        mFNameSrcRB = (RadioButton)findViewById(R.id.fNameSearch);
        mLNameSrcRB = (RadioButton)findViewById(R.id.lNameSearch);
        mUniSrcRb = (RadioButton)findViewById(R.id.uniSearch);

        mUniSrcRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSearchBar.addTextChangedListener(getUniIputTextWatcher());
                    mUserList.removeAllViews();
                    mSearchBar.setText("");
                }else{
                    mSearchBar.removeTextChangedListener(getUniIputTextWatcher());
                }
            }
        });

        mFNameSrcRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUserList.removeAllViews();
                mSearchBar.setText("");
            }
        });

        mLNameSrcRB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUserList.removeAllViews();
                mSearchBar.setText("");
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFNameSrcRB.isChecked()){
                    findByNameQuery("firstName");
                }else if(mLNameSrcRB.isChecked()){
                    findByNameQuery("lastName");
                } else {
                    mDatabase.child("users").orderByChild("university")
                            .equalTo(mSearchBar.getText().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child :
                                            dataSnapshot.getChildren()) {

                                        User us = child.getValue(User.class);
                                        addUserToList(us, child);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });
    }

    private void findByNameQuery(final String child) {
        final String query = mSearchBar.getText().toString();

        mDatabase.child("users").orderByChild(child)
                .startAt(query)
                .endAt(query + "~")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child :
                                dataSnapshot.getChildren()) {

                            User us = child.getValue(User.class);
                            addUserToList(us, child);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addUserToList(final User us, DataSnapshot child){
        final String uid = (String)child.getKey();

        if(uid.equals(mCurrUid)){
            return;
        }
        StorageReference storageRef = mStorage.child("profilePics")
                .child(child.getKey());

        if(storageRef.getPath().isEmpty()){
            storageRef = mStorage.child("profilePics").child("default-profile-picture.png");
        }


        TextView user = new TextView(UserSearchActivity.this);
        final Button chat = new Button(UserSearchActivity.this);
        LinearLayout userField = new LinearLayout(UserSearchActivity.this);
        final ImageView userProfilePic = new ImageView(UserSearchActivity.this);

        final StorageReference profilePicsRef = mStorage.child("profilePics");
        final StorageReference[] userProfilePicRef = {mStorage};
        profilePicsRef.child(uid).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userProfilePicRef[0] = profilePicsRef.child(uid);
                        setPicture(userProfilePicRef[0], userProfilePic);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                userProfilePicRef[0] = profilePicsRef.child("default-profile-picture.png");
                setPicture(userProfilePicRef[0], userProfilePic);
            }
        });

        user.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        chat.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        userField.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.HORIZONTAL));

        userProfilePic.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        Glide.with(UserSearchActivity.this)
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .into(userProfilePic);

        chat.setText("Chat");
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                String[] params = { uid ,us.getFirstName(), us.getLastName()};
                Intent chatPage = new Intent(UserSearchActivity.this, ChatActivity.class);

                b.putStringArray("userInfo", params);
                chatPage.putExtras(b);

                UserSearchActivity.this.startActivity(chatPage);
                Log.d("UserInfo: ", us.getFirstName() + " " + us.getLastName());
                Log.d("UserInfo: ", uid);
            }
        });

        userField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle id = new Bundle();
                id.putString("uid", uid);

                Intent chatPage = new Intent(UserSearchActivity.this, ProfilePageActivity.class);
                chatPage.putExtras(id);

                UserSearchActivity.this.startActivity(chatPage);
                Log.d("UserInfo: ", us.getFirstName() + " " + us.getLastName());
                Log.d("UserInfo: ", uid);
            }
        });

        user.setText(us.getFirstName() + " " + us.getLastName());

        userField.addView(userProfilePic);
        userField.addView(user);
        userField.addView(chat);

        mUserList.addView(userField);
    }

    private TextWatcher getUniIputTextWatcher(){
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DatabaseReference uniRef = mDatabase.child("universities");

                uniRef.orderByValue()
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

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserSearchActivity.this,
                                android.R.layout.simple_dropdown_item_1line,
                                values);
                        mSearchBar.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(UserSearchActivity.this,
                                "Can't get users, please try again later.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void setPicture(StorageReference ref, ImageView view){
        Glide.with(UserSearchActivity.this)
                .using(new FirebaseImageLoader())
                .load(ref)
                .into(view);
    }
}
