package com.scoll.teacher_parentmessagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scoll.teacher_parentmessagingapp.Adapter.MessageAdapter;
import com.scoll.teacher_parentmessagingapp.Model.MessageObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    // variables
    private RecyclerView mChat;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    ArrayList<MessageObject> messageList;
    String chatID;
    EditText mMessage;
    DatabaseReference mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // initializing chatID
        chatID = getIntent().getExtras().getString("chatID");
        DatabaseReference mUser;

        // initialing the sendBtn message button
        Button mSendBtn = findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // initializing messageList
        messageList = new ArrayList<>();

        // calling the functions
        initializeRecyclerView();
        getChatMessages();
    }

    // sendMessage function
    private void sendMessage(){
        // grabs the EditText
        mMessage = findViewById(R.id.messageInput);

        if(!mMessage.getText().toString().isEmpty()){
            // getting the messageId variable from the ChatListAdapter
            // database reference - goes into chat and chatId and pushes to create a new message
            DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push();

            Map newMessageMap = new HashMap<>();
            newMessageMap.put("message", mMessage.getText().toString());
            newMessageMap.put("creatorId", FirebaseAuth.getInstance().getUid());
//            newMessageMap.put("receiverId", mUser);

            newMessageDB.updateChildren(newMessageMap);
        }
        //clearing the editText field
        mMessage.setText(null);
    }

    // displaying messages from the FireBase DB
    private void getChatMessages() {
        FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).addChildEventListener(new ChildEventListener() {
            @Override

            // onChildAdded will get all the "children" in the DB, when we add a child it will be called again
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String message = "";
                    String creatorID = "";
                    String receiverID = "";
//                    String messageTime = "";

                    // if its null the app will crash
                    if(dataSnapshot.child("message").getValue() != null)
                        message = dataSnapshot.child("message").getValue().toString();

                    if(dataSnapshot.child("creatorId").getValue() != null)
                        creatorID = dataSnapshot.child("creatorId").getValue().toString();

//                    if(dataSnapshot.child("receiverId").getValue() != null)
//                        receiverID = dataSnapshot.child("receiverId").getValue().toString();
//
//                    if(dataSnapshot.child("messageTime").getValue() != null)
//                        messageTime = dataSnapshot.child("messageTime").getValue().toString();

                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, receiverID, message);
                    messageList.add(mMessage);

                    // scrolls down to the last message
                    mChatLayoutManager.scrollToPosition(messageList.size()-1);

                    // updates mChatAdapter and notifies that something changed
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    // function to initialize RecyclerView
    private void initializeRecyclerView() {
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);

        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }
}