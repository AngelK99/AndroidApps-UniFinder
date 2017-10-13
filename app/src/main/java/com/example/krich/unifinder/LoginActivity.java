package com.example.krich.unifinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.krich.unifinder.models.User;
import com.example.krich.unifinder.utils.PassHasher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Login");
        setContentView(R.layout.activity_login);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        Bundle extras = this.getIntent().getExtras();
        if(extras != null){
            String[] params = extras.getStringArray("loginParams");

            mEmail.setText(params[0]);
            mPassword.setText(params[1]);
        }else {
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);

            mEmail.setText(prefs.getString("email", ""));
            mPassword.setText(prefs.getString("password",""));
        }
    }

    protected void login(View v){
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(LoginActivity.this, "Email is required.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Password is required.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(email.length() > 30) {
            Toast.makeText(this, "Please enter valid email!", Toast.LENGTH_LONG).show();
            return;
        }

        if(password.length() > 36){
            Toast.makeText(this, "Password must be between 1 and 36 symbols!", Toast.LENGTH_LONG).show();
            return;
        }
        
        final ProgressDialog loggingInProgress = new ProgressDialog(this);
        loggingInProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loggingInProgress.setTitle("Logging in..");
        loggingInProgress.show();

        PassHasher ph = new PassHasher();
        String hashedPass = ph.Hash(password);

        mAuth.signInWithEmailAndPassword(email, hashedPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference ref = mDatabase.getReference("users/" + user.getUid());

                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User u = dataSnapshot.getValue(User.class);

                                    if(u.getHasLogged()) {
                                        Intent pp = new Intent(LoginActivity.this, HomePageActivity.class);
                                        LoginActivity.this.startActivity(pp);

                                        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("email", email);
                                        editor.putString("password", password);
                                        editor.commit();

                                        MainActivity.ma.finish();
                                        finish();
                                    }
                                    else{
                                        Intent ps = new Intent(LoginActivity.this, FirstLoginActivity.class);
                                        LoginActivity.this.startActivity(ps);

                                        MainActivity.ma.finish();
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    loggingInProgress.cancel();
                                    Toast.makeText(LoginActivity.this,
                                            "Database error, please try again later.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            loggingInProgress.cancel();
                            Toast.makeText(LoginActivity.this,
                                    "Authentication failed. Wrong username and/or password.",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }
}
