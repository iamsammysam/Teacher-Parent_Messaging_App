package com.scoll.teacher_parentmessagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private RecyclerView Chat;
    private RecyclerView.Adapter ChatAdapter;
    private RecyclerView.LayoutManager ChatLayoutManager;

    ArrayList<MessageObject> messageList;
    FirebaseTranslator englishSpanishTranslator;
    String chatID;

    EditText messageInput;
    TextView messageTranslation;

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

        if (!messageInput.getText().toString().isEmpty()) {
            // getting the messageId variable from the ChatListAdapter
            // database reference - goes into chat and chatId and pushes to create a new message
            DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).push();

            // sends message content on activity_chat layout to the database
            Map newMessageMap = new HashMap<>();
            newMessageMap.put("message", messageInput.getText().toString());
            newMessageMap.put("creatorId", FirebaseAuth.getInstance().getUid());

            newMessageDB.updateChildren(newMessageMap);
        }
        //clearing the editText field
        messageInput.setText(null);
    }

    // trial to translate the message from the DB... not workinf
//    // translation feature fireBase ML kit
//    public void downloadTranslatorAndTranslate() {
//        FirebaseTranslatorOptions options =
//                new FirebaseTranslatorOptions.Builder()
//                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
//                        .setTargetLanguage(FirebaseTranslateLanguage.ES)
//                        .build();
//
//        englishSpanishTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
//
//        //download models if needed
//        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
//                .requireWifi()
//                .build();
//
//        englishSpanishTranslator.downloadModelIfNeeded(conditions)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void v) {
//                        // Model downloaded successfully. Okay to start translating.
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Model couldn't be downloaded or other internal error.
//                    }
//                });
//
//    }
//
//    public void translate(final String message){
//        englishSpanishTranslator.translate(message)
//                .addOnSuccessListener(new OnSuccessListener<String>() {
//                    @Override
//                    public void onSuccess(@NonNull String translatedText) {
//                        message.setText(translatedText);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        message.setText(e.getMessage());
//                    }
//                });
//    }


    // displaying messages from the FireBase DB
    private void getChatMessages() {
        FirebaseDatabase.getInstance().getReference().child("chat").child(chatID).addChildEventListener(new ChildEventListener() {
            @Override

            // onChildAdded will get all the "children" in the DB, when we add a child it will be called again
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()) {
                    String message = "";
                    String creatorID = "";
                    String receiverID = "";
                    // String messageTime = "";

                    // translate here???

                    // if its null the app will crash
                    if (dataSnapshot.child("message").getValue() != null)
                        message = dataSnapshot.child("message").getValue().toString();

                    if (dataSnapshot.child("creatorId").getValue() != null)
                        creatorID = dataSnapshot.child("creatorId").getValue().toString();

//                    if(dataSnapshot.child("receiverId").getValue() != null)
//                        receiverID = dataSnapshot.child("receiverId").getValue().toString();
//
//                    if(dataSnapshot.child("messageTime").getValue() != null)
//                        messageTime = dataSnapshot.child("messageTime").getValue().toString();

                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, receiverID, message);
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