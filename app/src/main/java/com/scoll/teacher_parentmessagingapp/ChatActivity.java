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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.scoll.teacher_parentmessagingapp.Adapter.MessageAdapter;
import com.scoll.teacher_parentmessagingapp.Model.MessageObject;
import com.scoll.teacher_parentmessagingapp.Model.UserObject;
import com.scoll.teacher_parentmessagingapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    // variables
    private RecyclerView Chat;
    private RecyclerView.Adapter ChatAdapter;
    private RecyclerView.LayoutManager ChatLayoutManager;

    DatabaseReference referenceDB;
    ArrayList<MessageObject> messageList;
    EditText messageInput;
    String chatID;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // initializing chatID
        chatID = getIntent().getExtras().getString("chatID");

        // initialing the sendBtn message button
        Button SendBtn = findViewById(R.id.sendBtn);
        SendBtn.setOnClickListener(new View.OnClickListener() {
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
    private void sendMessage() {
        // grabs the EditText
        messageInput = findViewById(R.id.messageInput);

        // fetching data from DB (reference to database)
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceDB = FirebaseDatabase.getInstance().getReference().child("user").child(userID);

        Query query = referenceDB;
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = "";
                String language = "";

                if (dataSnapshot.child("username").getValue() != null)
                    username = dataSnapshot.child("username").getValue().toString();

                if (dataSnapshot.child("language").getValue() != null)
                    language = dataSnapshot.child("language").getValue().toString();

                if (!messageInput.getText().toString().isEmpty()) {
                    // database reference - goes into chat and chatId and pushes to create a new message
                    DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push();

                    // sends message content on activity_chat layout to the database
                    Map newMessageMap = new HashMap<>();
                    newMessageMap.put("message", messageInput.getText().toString());
                    newMessageMap.put("creatorId", FirebaseAuth.getInstance().getUid());
                    newMessageMap.put("username", username);
                    newMessageMap.put("language", language);

                    newMessageDB.updateChildren(newMessageMap);
                }

                //clearing the editText field
                messageInput.setText(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // displaying messages from the FireBase DB
    private void getChatMessages() {
        FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).addChildEventListener(new ChildEventListener() {
            @Override

            // onChildAdded will get all the "children" in the DB, when we add a child it will be called again
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()) {
                    String message = "";
                    String creatorID = "";
                    String username = "";
                    String language = "";

                    // if its null the app will crash
                    if (dataSnapshot.child("message").getValue() != null)
                        message = dataSnapshot.child("message").getValue().toString();

                    if (dataSnapshot.child("creatorId").getValue() != null)
                        creatorID = dataSnapshot.child("creatorId").getValue().toString();

                    if (dataSnapshot.child("username").getValue() != null)
                        username = dataSnapshot.child("username").getValue().toString();

                    if (dataSnapshot.child("language").getValue() != null)
                        language = dataSnapshot.child("language").getValue().toString();

                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, username, message, language);
                    messageList.add(mMessage);

                    // scrolls down to the last message
                    ChatLayoutManager.scrollToPosition(messageList.size() - 1);

                    // updates mChatAdapter and notifies that something changed
                    ChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // function to initialize RecyclerView
    private void initializeRecyclerView() {
        Chat = findViewById(R.id.messageList);
        Chat.setNestedScrollingEnabled(false);
        Chat.setHasFixedSize(false);

        ChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        Chat.setLayoutManager(ChatLayoutManager);
        ChatAdapter = new MessageAdapter(messageList);
        Chat.setAdapter(ChatAdapter);
    }
}