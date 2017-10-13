package com.example.krich.unifinder;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        setTitle("Find People");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

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
                }else{
                    mSearchBar.removeTextChangedListener(getUniIputTextWatcher());
                }
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

        Toast.makeText(UserSearchActivity.this, query, Toast.LENGTH_SHORT).show();

        mDatabase.child("users").orderByChild(child)
                .startAt(query)
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
        final StorageReference storageRef = mStorage.child("profilePics")
                .child(child.getKey());


        TextView user = new TextView(UserSearchActivity.this);
        final Button chat = new Button(UserSearchActivity.this);
        LinearLayout userField = new LinearLayout(UserSearchActivity.this);
        final ImageView userProfilePic = new ImageView(UserSearchActivity.this);

        user.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        chat.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
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
                Log.d("QueryInfo: ", "works " + s);
                DatabaseReference uniRef = mDatabase.child("universities");

                uniRef.orderByValue().startAt(s.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }
}
