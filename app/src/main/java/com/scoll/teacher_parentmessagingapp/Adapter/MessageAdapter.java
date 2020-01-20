// where we manage and display messages (same as message collection)

package com.scoll.teacher_parentmessagingapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.scoll.teacher_parentmessagingapp.Model.MessageObject;
import com.scoll.teacher_parentmessagingapp.R;

import java.util.ArrayList;

// implementing the viewHolder (gets the data from the xml)
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageListViewHolder> {

    // passing the variables
    public final int MSG_TYPE_RIGHT = 0;
    public final int MSG_TYPE_LEFT = 1;

    // initializing variables
    String userID;
    String userLanguage;
    DatabaseReference referenceDB;
    ArrayList<MessageObject> messageList;
    //ArrayList<MessageObject> translationList;

    // constructor
    public MessageAdapter(ArrayList<MessageObject> messageList) {
        this.messageList = messageList;
        languageFromDB();
    }

    public void languageFromDB(){
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceDB = FirebaseDatabase.getInstance().getReference().child("user").child(userID);

        Query query = referenceDB;
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //String userlanguage = "";

                if (dataSnapshot.child("language").getValue() != null)
                    userLanguage = dataSnapshot.child("language").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void translateText(String message, FirebaseTranslator langTranslator, final MessageListViewHolder holder) {
        // translate source text to language defined by user
        langTranslator.translate(message)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                holder.message.setText(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });

    }

    public void downloadTranslatorAndTranslate(final String message, String langCode, final MessageListViewHolder holder) {
        // get source language id from bcp code
        int sourceLanguage = FirebaseTranslateLanguage.languageForLanguageCode(langCode);
        int targetLanguage = 0;

        if (userLanguage.equals("Spanish")){
            targetLanguage = FirebaseTranslateLanguage.ES;

        } else if (userLanguage.equals("English")){
            targetLanguage = FirebaseTranslateLanguage.EN;

        } else if (userLanguage.equals("Korean")){
            targetLanguage = FirebaseTranslateLanguage.KO;
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
                                translateText(message, langTranslator, holder);
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

    public String translateTextToLanguage(final String message, final MessageListViewHolder holder){
        // identifies the language of the messages on the DB
        FirebaseLanguageIdentification languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        languageIdentifier.identifyLanguage(message)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode != "und") {
                                    Log.d("translator", "lang "+languageCode);
                                    // download translator for the identified language
                                    // and translate the entered text into english
                                    downloadTranslatorAndTranslate(message, languageCode, holder);
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

    @NonNull
    @Override
    public MessageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // making the call for the item_chat (depending on the firebaseUser position)
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left, null, false);
            return new MessageAdapter.MessageListViewHolder(view);

        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right, null, false);
            return new MessageAdapter.MessageListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListViewHolder holder, int position) {
        holder.message.setText(messageList.get(position).getMessage());
        holder.sender.setText(messageList.get(position).getSenderUsername());

        //String translation = (translateTextToLanguage(messageList.get(position).getMessage(), holder));
        //translationlist.add(translation);

        // check if I can save this translation on the DB and keep translating only the last message
        // String translation = translateTextToLanguage(messageList.get(position).getMessage(), holder);
    }

    @Override
    public int getItemCount() {
        return (messageList == null) ? 0 : messageList.size();
    }

    public class MessageListViewHolder extends RecyclerView.ViewHolder {
        public TextView message, sender;
        public RelativeLayout itemLayout;

        public MessageListViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            sender = itemView.findViewById(R.id.sender);
            //mMessageTime = view.findViewById(R.id.messageTime);
            itemLayout = itemView.findViewById(R.id.itemLayout);
        }
    }

    // getting text message position on messageList
    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}