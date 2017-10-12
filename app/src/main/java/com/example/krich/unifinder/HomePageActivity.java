package com.example.krich.unifinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class HomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private LinearLayout mUserList;
    private FirebaseStorage mStorage;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_myProfile:
                openUserProfile();
                return true;
            case R.id.action_chats:
                //openUserProfile();
                return true;
            case R.id.action_logOut:
                //openUserProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home page");
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mUserList = (LinearLayout)findViewById(R.id.userList);

        getUsers();
    }

    private void getUsers(){
        DatabaseReference ref = mDatabase.getReference("users");
        Log.d("USer Test: ", "works");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String currUid = mAuth.getCurrentUser().getUid();

                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    final User us = childDataSnapshot.getValue(User.class);
                    final String uid = (String)childDataSnapshot.getKey();
                    final StorageReference storageref = mStorage.getReference()
                            .child("profilePics")
                            .child(childDataSnapshot.getKey());



                    if(uid.equals(currUid)){
                        continue;
                    }

                    TextView user = new TextView(HomePageActivity.this);
                    final Button chat = new Button(HomePageActivity.this);
                    LinearLayout userField = new LinearLayout(HomePageActivity.this);
                    final ImageView userProfilePic = new ImageView(HomePageActivity.this);
                    Button viewProfile = new Button(HomePageActivity.this);

                    user.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    chat.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    userField.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.HORIZONTAL));

                    userProfilePic.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    ));

                    viewProfile.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    Glide.with(HomePageActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(storageref)
                            .into(userProfilePic);

                    chat.setText("Chat");
                    chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle b = new Bundle();
                            String[] params = { uid ,us.getFirstName(), us.getLastName()};
                            Intent chatPage = new Intent(HomePageActivity.this, ChatActivity.class);

                            b.putStringArray("userInfo", params);
                            chatPage.putExtras(b);

                            HomePageActivity.this.startActivity(chatPage);
                            Log.d("UserInfo: ", us.getFirstName() + " " + us.getLastName());
                            Log.d("UserInfo: ", uid);
                        }
                    });

                    viewProfile.setText("View Profile");
                    viewProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle b = new Bundle();
                            String[] params = { uid ,us.getFirstName(), us.getLastName()};
                            Intent chatPage = new Intent(HomePageActivity.this, ProfilePageActivity.class);

                            b.putStringArray("userInfo", params);
                            chatPage.putExtras(b);

                            HomePageActivity.this.startActivity(chatPage);
                            Log.d("UserInfo: ", us.getFirstName() + " " + us.getLastName());
                            Log.d("UserInfo: ", uid);
                        }
                    });

                    user.setText(us.getFirstName() + " " + us.getLastName());

                    userField.addView(userProfilePic);
                    userField.addView(user);
                    userField.addView(viewProfile);
                    userField.addView(chat);

                    mUserList.addView(userField);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomePageActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openUserProfile(){
        String uid = mAuth.getCurrentUser().getUid();

        Bundle id = new Bundle();
        id.putString("uid", uid);
        Intent profilePage = new Intent(HomePageActivity.this, ProfilePageActivity.class);
        profilePage.putExtras(id);

        this.startActivity(profilePage);
    }
}
