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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
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
    EditText mMessageInput;
    TextView mMessage;
    Task<String> mMessageTranslation;
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
    private void sendMessage() {
        // grabs the EditText
        mMessageInput = findViewById(R.id.messageInput);

        if (!mMessageInput.getText().toString().isEmpty()) {
            // getting the messageId variable from the ChatListAdapter
            // database reference - goes into chat and chatId and pushes to create a new message
            DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push();

            // sends message content on activity_chat layout to the database
            Map newMessageMap = new HashMap<>();
            newMessageMap.put("message", mMessageInput.getText().toString());
            newMessageMap.put("creatorId", FirebaseAuth.getInstance().getUid());
            newMessageMap.put("translation", mMessageTranslation.getResult());

            newMessageDB.updateChildren(newMessageMap);
        }
        //clearing the editText field
        mMessageInput.setText(null);
    }

    // displaying messages from the FireBase DB
    private void getChatMessages() {
        FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).addChildEventListener(new ChildEventListener() {
            @Override

            // onChildAdded will get all the "children" in the DB, when we add a child it will be called again
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String message = "";
                    String translation = "";
                    String creatorID = "";
                    String receiverID = "";
                    // String messageTime = "";

                    // if its null the app will crash
                    if (dataSnapshot.child("message").getValue() != null)
                        message = dataSnapshot.child("message").getValue().toString();

                    if (dataSnapshot.child("translation").getValue() != null)
                        translation = dataSnapshot.child("translation").getValue().toString();

                    if (dataSnapshot.child("creatorId").getValue() != null)
                        creatorID = dataSnapshot.child("creatorId").getValue().toString();

//                    if(dataSnapshot.child("receiverId").getValue() != null)
//                        receiverID = dataSnapshot.child("receiverId").getValue().toString();
//
//                    if(dataSnapshot.child("messageTime").getValue() != null)
//                        messageTime = dataSnapshot.child("messageTime").getValue().toString();

                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, receiverID, message, translation);
                    messageList.add(mMessage);

                    // scrolls down to the last message
                    mChatLayoutManager.scrollToPosition(messageList.size() - 1);

                    // updates mChatAdapter and notifies that something changed
                    mChatAdapter.notifyDataSetChanged();
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
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);

        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }

    // Translation feature
    // Create an English-Spanish translator:
    FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.ES)
            .build();

    final FirebaseTranslator englishSpanishTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

    FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
            .requireWifi()
            .build();

    // checking model download
    public void downloadModelIfNeeded(){
        englishSpanishTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                translate();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Model couldn’t be downloaded or other internal error.
            }
        });
    }

    // calling translation function
    public void translate() {
        mMessage = findViewById(R.id.message);
        final String text = mMessage.getText().toString();

        englishSpanishTranslator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(@NonNull String translatedText) {
                mMessageTranslation = englishSpanishTranslator.translate(text);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}