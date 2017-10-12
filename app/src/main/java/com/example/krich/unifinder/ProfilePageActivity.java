package com.example.krich.unifinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krich.unifinder.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfilePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private TextView mHelloUserView;
    private StorageReference mStorageRef;
    private LinearLayout mProfilePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        String userId = this.getIntent().getExtras().getString("uid");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mProfilePanel = (LinearLayout) findViewById(R.id.profileInfo);

        createUserProfile(userId);
    }

    private void createUserProfile(String uId){
        DatabaseReference ref = mDatabase.getReference("users/" + uId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                buildProfile(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfilePageActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildProfile(User u){
        TextView fName = new TextView(this);
        TextView lName = new TextView(this);
        TextView sex = new TextView(this);

        fName.setText("First Name: " + u.getFirstName());
        mProfilePanel.addView(fName);

        if(!u.getMidName().isEmpty()){
            TextView midName = new TextView(this);
            midName.setText("Middle Name: " + u.getMidName());
            mProfilePanel.addView(midName);
        }

        lName.setText("Last Name: " + u.getLastName());
        mProfilePanel.addView(lName);

        if(u.getSex().equals("m")){
            sex.setText("Sex: Male");
        } else{
            sex.setText("Sex: Female");
        }
        mProfilePanel.addView(sex);

        if(!u.getTelHidden() && !u.getTelNum().isEmpty()){
            TextView tel = new TextView(this);
            tel.setText("Telephone Number: " + u.getTelNum());
            mProfilePanel.addView(tel);
        }
    }
}
