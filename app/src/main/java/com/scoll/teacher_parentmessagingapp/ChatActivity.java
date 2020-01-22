package com.scoll.teacher_parentmessagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.scoll.teacher_parentmessagingapp.Adapter.MessageAdapter;
import com.scoll.teacher_parentmessagingapp.Model.MessageObject;
import com.scoll.teacher_parentmessagingapp.Model.UserObject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ChatActivity extends AppCompatActivity {



    // variables
    private RecyclerView Chat;
    private RecyclerView.Adapter ChatAdapter;
    private RecyclerView.LayoutManager ChatLayoutManager;

    DatabaseReference referenceDB;
    ArrayList<MessageObject> messageList;
    ArrayList<UserObject> userList;

    String userLanguage;
    String chatID;
    String userID;
    String messageTranslation;
    EditText messageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // initializing chatID
        chatID = getIntent().getExtras().getString("chatID");
        userList = new ArrayList<>();

        // initialing the sendBtn message button
        Button SendBtn = findViewById(R.id.sendBtn);
        SendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                languageFromDB();

                messageInput = findViewById(R.id.messageInput);
                String message = messageInput.getText().toString();
                translateTextToLanguage(message);
            }
        });

        // initializing messageList
        messageList = new ArrayList<>();

        // calling the functions
        initializeRecyclerView();
        getChatMessages();
    }

    public void languageFromDB(){
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceDB = FirebaseDatabase.getInstance().getReference().child("user").child(userID);

        Query query = referenceDB;
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("language").getValue() != null)
                    userLanguage = dataSnapshot.child("language").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

//    private boolean isStarted = false;
    public void translateText(final String message, final FirebaseTranslator langTranslator) {
        // translate source text to language defined by user
        langTranslator.translate(message)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String translatedtext) {
                                   messageTranslation = translatedtext;


                                // call database here to update message translation
                                }
                            })

                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
//            isStarted = true;
        }

    public void downloadTranslatorAndTranslate(final String message, String langCode) {
        // get source language id from bcp code

        int sourceLanguage = FirebaseTranslateLanguage.languageForLanguageCode(langCode);
        //int sourceLanguage = 0;
        int targetLanguage = 0;

        if (userLanguage.equals("Spanish")){
            //sourceLanguage = FirebaseTranslateLanguage.ES;
            targetLanguage = FirebaseTranslateLanguage.EN;

        } else if (userLanguage.equals("English")) {
            //sourceLanguage = FirebaseTranslateLanguage.EN;
            targetLanguage = FirebaseTranslateLanguage.ES;

        } else if (userLanguage.equals("Korean")) {
            //sourceLanguage = FirebaseTranslateLanguage.EN;
            targetLanguage = FirebaseTranslateLanguage.KO;

        } else if (userLanguage.equals("Chinese")) {
            //sourceLanguage = FirebaseTranslateLanguage.EN;
            targetLanguage = FirebaseTranslateLanguage.ZH;
        }

        // create translator for source and target languages
        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(sourceLanguage)
                        .setTargetLanguage(targetLanguage)
                        .build();

        final FirebaseTranslator langTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        //download language models if needed
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        langTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Log.d("translator", "downloaded lang model");
                                // after making sure language models are available make translation
                                translateText(message, langTranslator);

                                try {
                                    sendMessage();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // error message
                            }
                        });
    }

    public String translateTextToLanguage(final String message){
        // identifies the language of the messages on the DB
        FirebaseLanguageIdentification languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        languageIdentifier.identifyLanguage(message)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode != "und") {
                                    Log.d("translator", "lang "+languageCode);
                                    // download translator for the identified language and translate the entered text
                                    downloadTranslatorAndTranslate(message, languageCode);
                                } else {
                                    // error message: language model not downloaded.
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // error message: language not identified.
                            }
                        });
        return message;
    }

    // sendMessage function
    private void sendMessage() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);

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
                        newMessageMap.put("translation", messageTranslation);

                        newMessageDB.updateChildren(newMessageMap);

                        //clearing the editText field
                        messageInput.setText(null);
                    }
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
                    String translation = "";
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

                    if (dataSnapshot.child("translation").getValue() != null)
                        translation = dataSnapshot.child("translation").getValue().toString();

                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, username, message, language, translation);
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