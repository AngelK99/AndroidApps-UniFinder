package com.example.krich.unifinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.krich.unifinder.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private EditText mMsgText;
    private Button mMsgSendBtn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mChatUserRef;
    private SimpleDateFormat mDateFormat;
    private DatabaseReference mPersonToChatRef;
    private ScrollView mScrollView;
    private Runnable mScrollDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String[] userInfo = this.getIntent().getExtras().getStringArray("userInfo");
        setTitle("Chat with " + userInfo[1] + " " + userInfo[2]);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mMsgText = (EditText) findViewById(R.id.msgInput);
        mMsgSendBtn = (Button) findViewById(R.id.msgSendBtn);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mScrollView = (ScrollView)findViewById(R.id.chat);
        mScrollDown = new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        };

        mChatUserRef = mDatabase.getReference().child("users")
                .child(mAuth.getCurrentUser()
                        .getUid())
                        .child("chats")
                        .child(userInfo[0]);
        mPersonToChatRef = mDatabase.getReference().child("users")
                .child(userInfo[0])
                .child("chats")
                .child(mAuth.getCurrentUser().getUid());

        mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        mMsgSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date msgTime = new Date();
                String msg = mMsgText.getText().toString();
                String time = mDateFormat.format(msgTime);

                mChatUserRef.child(time).setValue(new ChatMessage(msg, "0"));
                mPersonToChatRef.child(time).setValue(new ChatMessage(msg, "1"));
            }
        });

        mChatUserRef.addChildEventListener(new ChildEventListener(){

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TextView msgView = new TextView(ChatActivity.this);
                ChatMessage msg = dataSnapshot.getValue(ChatMessage.class);
                LinearLayout chat = (LinearLayout)findViewById(R.id.msgs);

                Log.d("ChatInfo: ", msg.toString());

                msgView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                if(msg.getSender().equals("0")) {
                    msgView.setGravity(Gravity.RIGHT);
                }


                msgView.setText(msg.getText());
                chat.addView(msgView);

                mScrollView.post(mScrollDown);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

