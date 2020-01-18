// where we manage and display messages (same as message collection)

package com.scoll.teacher_parentmessagingapp.Adapter;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.scoll.teacher_parentmessagingapp.Model.MessageObject;
import com.scoll.teacher_parentmessagingapp.R;

import java.util.ArrayList;

// implementing the viewHolder (gets the data from the xml)
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageListViewHolder> {

    // passing the variables
    public final int MSG_TYPE_RIGHT = 0;
    public final int MSG_TYPE_LEFT = 1;

    // initializing messageList
    ArrayList<MessageObject> messageList;
    //this works keep this!!!
    //FirebaseTranslator englishSpanishTranslator;


    // constructor
    public MessageAdapter(ArrayList<MessageObject> messageList) {
        this.messageList = messageList;
   }

    public void translateText(String message, FirebaseTranslator langTranslator, final MessageListViewHolder holder) {
        //translate source text to english
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
        //get source language id from bcp code
        int sourceLanguage = FirebaseTranslateLanguage.languageForLanguageCode(langCode);

        //create translator for source and target languages
        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(sourceLanguage)
                        .setTargetLanguage(FirebaseTranslateLanguage.EN)
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
                                Log.d("translator", "downloaded lang  model");
                                //after making sure language models are available
                                //perform translation
                                translateText(message, langTranslator, holder);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //"Problem in translating the text entered",

                            }
                        });
    }

    public void translateTextToEnglish(final String message, final MessageListViewHolder holder){

        //First identify the language of the entered text
        FirebaseLanguageIdentification languageIdentifier = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();

        languageIdentifier.identifyLanguage(message)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode != "und") {
                                    Log.d("translator", "lang "+languageCode);
                                    //download translator for the identified language
                                    // and translate the entered text into english
                                    downloadTranslatorAndTranslate(message, languageCode, holder);
                                } else {
                                   // "Problem in identifying language of the text entered",
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // "Problem in identifying language of the text entered",
                            }
                        });
    }


          // this works! keep this!!!
//        // translation feature fireBase ML kit
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
//                        // Model couldn’t be downloaded or other internal error.
//                    }
//                });
//    }
//
//    public void translate(final String message, final MessageListViewHolder holder) {
//        englishSpanishTranslator.translate(message)
//                .addOnSuccessListener(new OnSuccessListener<String>() {
//                    @Override
//                    public void onSuccess(@NonNull String messageTranslation) {
//                        holder.mMessage.setText(messageTranslation);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        holder.mMessage.setText(e.getMessage());
//                    }
//                });



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
        holder.sender.setText(messageList.get(position).getSenderId());

        // this works! keep this!!!
        // calling the translate function on messageList
        // translate(messageList.get(position).getMessage(), holder);

        translateTextToEnglish(messageList.get(position).getMessage(), holder);
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
            //mReceiver = itemView.findViewById(R.id.receiver);
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