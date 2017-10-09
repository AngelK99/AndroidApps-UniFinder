package com.example.krich.unifinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private User mUser;

    private TextView mHelloUserView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mHelloUserView = (TextView) findViewById(R.id.helloUser);
    }

    private void createUserProfile(){
        DatabaseReference ref = mDatabase.getReference("users/" + mAuth.getCurrentUser().getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);

                mHelloUserView.setText("Profile of " + mUser.getFirstName() + " " + mUser.getLastName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfilePageActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
