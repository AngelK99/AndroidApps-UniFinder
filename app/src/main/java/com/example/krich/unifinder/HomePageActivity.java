package com.example.krich.unifinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private LinearLayout mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home page");
        setContentView(R.layout.activity_home_page);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
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

                    if(uid.equals(currUid)){
                        continue;
                    }

                    TextView user = new TextView(HomePageActivity.this);
                    final Button chat = new Button(HomePageActivity.this);
                    LinearLayout userField = new LinearLayout(HomePageActivity.this);

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

                    user.setText(us.getEmail());

                    userField.addView(user);
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
}
